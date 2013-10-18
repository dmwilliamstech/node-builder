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

import org.activiti.engine.*
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.repository.Deployment
import org.activiti.engine.task.Task

class BusinessProcessTests {

    void testSimpleBusinessProcess() {

        // Create Activiti process engine
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();

        // Get Activiti services
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // Deploy the process definition
        repositoryService.createDeployment()
                .addClasspathResource("resources/SampleProcess.bpmn20.xml")
                .deploy();

        // Start a process instance
        String procId = runtimeService.startProcessInstanceByKey("financialReport").getId();

        // Get the first task
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy group: " + task.getName());
            assert task.getName() == "Write monthly financial report"
            // claim it
            taskService.claim(task.getId(), "fozzie");
        }

        // Verify Fozzie can now retrieve the task
        tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
        for (Task task : tasks) {
            System.out.println("Task for fozzie: " + task.getName());

            // Complete the task
            taskService.complete(task.getId());
        }

        assert taskService.createTaskQuery().taskAssignee("fozzie").count() == 0
        // Retrieve and claim the second task
        tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy group: " + task.getName());
            assert task.getName() == "Verify monthly financial report"
            taskService.claim(task.getId(), "kermit");
        }

        // Completing the second task ends the process
        for (Task task : tasks) {
            taskService.complete(task.getId());
        }

        assert taskService.createTaskQuery().taskAssignee("kermit").count() == 0

        // verify that the process is actually finished
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =
            historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());
    }



    void testSetVariableInBpmn(){
        Deployment deployment = ProcessEngineFactory.deployProcessDefinitionFromUrlWithProcessEngine("resources/setVariables.bpmn20.xml", "test", "test")
        assert deployment.name == "test"

        def processEngine = ProcessEngineFactory.defaultProcessEngine("test")
        def result = ProcessEngineFactory.runProcessWithVariables(processEngine, "setVariables", [myName: "Some Name"])
        assert result.myName == "New Name"
    }
}
