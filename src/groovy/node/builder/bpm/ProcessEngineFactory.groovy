/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package node.builder.bpm

import node.builder.Retryable
import org.activiti.engine.*
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.repository.Deployment
import org.activiti.engine.runtime.Execution
import org.activiti.engine.task.Task

@Category(Retryable)
class ProcessEngineFactory {
    static private final $lock = new Object[0]

    private ProcessEngineFactory(){
    }

    public static ProcessEngine defaultProcessEngine(String name){
        synchronized ($lock){
            def processEngine = ProcessEngines.getProcessEngine(name)
            if(processEngine  == null){
                processEngine = ProcessEngineConfiguration
                        .createStandaloneProcessEngineConfiguration()
                        .setJdbcUrl("jdbc:h2:activiti;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE")
                        .setDatabaseSchemaUpdate("true")
                        .setProcessEngineName(name)
                        .buildProcessEngine();
                ProcessEngines.registerProcessEngine(processEngine)
            }
            return processEngine
        }
    }

    public static def getResultFromProcess(processEngine, processInstance){
        RuntimeService runtimeService = processEngine.getRuntimeService()
        HistoryService historyService = processEngine.getHistoryService()
        TaskService    taskService    = processEngine.getTaskService()
        def result

        if(processInstance.ended){
            def histories = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.id).variableNameLike('result').singleResult()
            result = histories.value
            def start = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.id).variableNameLike('start').singleResult()
            result.data.start = start?.value

            def businessKey = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.id).variableNameLike('businessKey').singleResult()
            result.data.businessKey = businessKey?.value
        } else {
            def task = taskService.createTaskQuery().processInstanceId(processInstance.id).active().singleResult()
            if(task == null){
                result = runtimeService.getVariable(processInstance.getId(), "error") ?: runtimeService.getVariable(processInstance.getId(), "result")
                Execution execution = runtimeService.createExecutionQuery()
                        .processInstanceId(processInstance.getId())
                        .activityId("receiveTask")
                        .singleResult();

                runtimeService.signal(execution.getId())
            }else{
                result = task
            }
        }
        return result
    }

    public static Deployment deployProcessDefinitionFromUrlWithProcessEngine(String url, String engineName, String deploymentName = null){
        ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine(engineName)
        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(url)
                .name(deploymentName)
                .deploy();
        assert deployment != null

        return deployment
    }



    public static def runProcessWithBusinessKeyAndVariables(ProcessEngine processEngine, String processKey, String businessKey, Map variables){
        use(Retryable){
            def r = 0
            Closure handler = {
                log.warn("Activiti error detected retrying process - ${processKey} - ${businessKey}")
                businessKey = businessKey.replaceAll(/\-\d$/, '') + "-${++r}"
            }

            staticRetry3(handler, ActivitiOptimisticLockingException, 5, 500){
                RuntimeService runtimeService = processEngine.getRuntimeService()

                // Start a process instance
                def processInstance = runtimeService.startProcessInstanceByKey(processKey, businessKey, variables)

                // verify that the process is actually finished
                def result = getResultFromProcess(processEngine, processInstance)

                return result
            }
        }
    }

    static def staticRetry3(handler, clazz = Exception, retries = 1, timeout, c) {
        try {
            return c()
        } catch(e) {
            retries -= 1
            if(e.class == clazz && retries >= 0){
                handler(e)
                sleep(timeout)
                staticRetry3(handler, clazz, retries, timeout, c)
            }else{
                throw e
            }
        }
    }

    public static def runProcessWithVariables(ProcessEngine processEngine, String processKey, Map variables){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        def processInstance
        synchronized($lock) {
            // Start a process instance
            processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);
        }
        // verify that the process is actually finished

        def result = runtimeService.getVariable(processInstance.getId(), "error") ?: runtimeService.getVariable(processInstance.getId(), "result")

        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId())
                .activityId("receiveTask")
                .singleResult();
        runtimeService.signal(execution.getId());

        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =
            historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();

        return result
    }

    public static def taskToMap(task){
        def taskData = [:]
        task.properties.each { key, value ->
            taskData[key] = value.toString()
        }
        return taskData
    }

    public static def tasksForGroups(List groups){
        def tasks = []
        ProcessEngines.processEngines.each { name, processEngine ->
            processEngine.taskService.createTaskQuery()
                .taskCandidateGroupIn(groups).list().each{Task task ->
                tasks << taskToMap(task)
            }
        }
        return tasks
    }
}
