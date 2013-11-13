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

package node.builder

import node.builder.bpm.ProcessEngineFactory
import node.builder.bpm.ProcessResult
import node.builder.metrics.MetricEvents
import node.builder.metrics.MetricGroups
import org.activiti.engine.ActivitiObjectNotFoundException
import org.activiti.engine.task.Task
import org.springframework.security.core.GrantedAuthority

import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * WorkflowService
 * A service class encapsulates the core business logic of a Grails application
 */
class WorkflowService {
    def springSecurityService

    static transactional = true

    static def pool = Executors.newCachedThreadPool()
    static Map futures = [:]


    def createWithoutSaving(params, organizations){
        def workflowInstance = new Workflow(params)
        if(!params.tags.toString().empty){
            workflowInstance = addWorkflowTagsById(params.tags.split(','), workflowInstance)
        }
        workflowInstance.organizations = organizations
        return workflowInstance
    }

    def addWorkflowTagsById(ids, workflowInstance){
        WorkflowTag.all.each { tag-> workflowInstance.removeFromTags(tag) }
        WorkflowTag.findAllByIdInList(ids*.toLong()).each{tag -> workflowInstance.addToTags(tag)}
        return workflowInstance
    }

    def findAllByOrganizations(organizations, params){
        if(organizations == null || organizations.empty)
            return []

        if(springSecurityService.principal.authorities.find{GrantedAuthority auth -> auth.authority.contains('NBADMINS')}){
            return Workflow.list(params)
        }
        def workflows = Workflow.executeQuery(
                'from Workflow p where :organizations in elements(p.organizations) order by name',
                [organizations: organizations], params)
        return workflows
    }

    def getByOrganizations(id, organizations){
        if(organizations == null || organizations.empty)
            return null

        if(springSecurityService.principal.authorities.find{GrantedAuthority auth -> auth.authority.contains('NBADMINS')}){
            return Workflow.get(id)
        }
        def workflow = Workflow.executeQuery(
                'from Workflow p where p.id=:id and :organizations in elements(p.organizations)',
                [id: Long.parseLong(id), organizations: organizations])

        return workflow.empty ? null : workflow.first()
    }

    def filesFromResult(ProcessResult result){
        try{
            def files = []
            if(result?.data == null)
                return files

            result.data.each{ key, value ->
                if(key.toString().toLowerCase().matches(/.*file$/)){
                    files << [path: value, name: new File(value).name]
                }
            }

            return files
        }catch(e){
            e.printStackTrace()
        }
    }

    def completeTask(Workflow workflow, decision){
        def message = "Task '$workflow.task.name' was ${decision.toString().replaceAll(/e$/, '') + 'ed'}"
        Executors.newSingleThreadExecutor().execute {
            Workflow.withNewSession {
                workflow = workflow.refresh()
                workflow.state = WorkflowState.RUNNING
                workflow.message = "Running process ${workflow.processDefinitionKey} on workflow ${workflow.name}"
                workflow.save(validate: false, flush: true)
                def processInstanceId = workflow.task.processInstanceId.toString()

                ProcessEngineFactory.defaultProcessEngine(workflow.name).runtimeService.setVariable(processInstanceId, "userTaskDecision", decision)
                ProcessEngineFactory.defaultProcessEngine(workflow.name).getTaskService().complete(workflow.task.id)
                def result

                def processInstance = ProcessEngineFactory.defaultProcessEngine(workflow.name)
                        .runtimeService
                        .createProcessInstanceQuery()
                        .processInstanceId(processInstanceId).singleResult()?: [ended: true, id:processInstanceId]

                result = ProcessEngineFactory.getResultFromProcess(ProcessEngineFactory.defaultProcessEngine(workflow.name), processInstance)

                if(result instanceof ProcessResult){
                    processRunResult(result, workflow, result.data.start, result.data.businessKey)
                }else{
                    processRunResult(result, workflow, -1, "")
                }
            }
        }
        return [message: message, id: workflow.id]
    }

    def run(Workflow workflow) {
        def message = ""
        if(workflow.state.isRunning()){
            log.info("Workflow $workflow.name already running, ignoring request to run")
            return [message: "Workflow already running", workflow: workflow]
        }
        workflow.state = WorkflowState.RUNNING
        workflow.message = "Running process ${workflow.processDefinitionKey} on workflow ${workflow.name}"
        workflow.save(validate: false)

        futures.remove(workflow.name)
        futures.put(workflow.name, pool.submit(new Callable<ProcessResult>() {
                public ProcessResult call() {
                    def businessKey = "${workflow.name}-${java.util.UUID.randomUUID()}"
                    def start = System.currentTimeMillis()
                    def result
                    try{
                        def processEngine = ProcessEngineFactory.defaultProcessEngine(workflow.name)

                        log.metric(MetricEvents.START, MetricGroups.WORKFLOW, "", businessKey, workflow.name, "")

                        def variables = new HashMap();
                        variables.start = start
                        populateVariables(variables, workflow.properties, workflow.organizations.toList(), businessKey)

                        log.info "Running process ${workflow.processDefinitionKey} on workflow ${workflow.name}"
                        result = ProcessEngineFactory.runProcessWithBusinessKeyAndVariables(processEngine, workflow.processDefinitionKey, businessKey, variables)
                        log.info "Process ${workflow.processDefinitionKey} on workflow ${workflow.name} finished"

                    }catch(ActivitiObjectNotFoundException e){
                        log.error(e.getMessage())
                        e.printStackTrace()
                        workflow.state = WorkflowState.WARNING
                        workflow.message = "Unable to detect state of workflow, please add a receive task to your workflow"
                    }catch(e){
                        log.error(e.getMessage())
                        e.printStackTrace()
                        workflow.state = WorkflowState.ERROR
                        workflow.message = e.getMessage().substring(0,254)
                    }finally{
                        processRunResult(result, workflow, start, businessKey)
                    }
                    return new ProcessResult(workflow.message, workflow, businessKey)
                }
        }
        ));
        return [message: workflow.message, workflow: workflow]
    }


    def processRunResult(result, workflow, long start, businessKey) {
        if(result instanceof ProcessResult){
            workflow.message = "Process ${workflow.processDefinitionKey} on workflow ${workflow.name} finished - ${result?.message}"
            workflow.state = WorkflowState.OK

            result?.data?.status = workflow?.state
            result?.data?.artifactFiles = filesFromResult(result)

            def duration = -1
            if (result?.data?.repositoryDidChange) {
                duration = (System.currentTimeMillis() - start)
            }


            log.metric(MetricEvents.FINISH, MetricGroups.WORKFLOW, workflow.message, businessKey, workflow.name, "", result, duration)

        }else if(result instanceof Task){
            def task = (Task)result
            def name = task.name?: (task.description ?: task.id)
            workflow.message = "Process ${workflow.processDefinitionKey} on workflow ${workflow.name} waiting on ${name}"
            workflow.state = WorkflowState.WAITING
            workflow.task = ProcessEngineFactory.taskToMap(task)
        }
        Workflow.withTransaction {
            workflow.save(validate: false)
        }
    }

    def populateVariables(HashMap variables, workflow, organizations, GString businessKey) {
        def config =  Config.getGlobalConfig()
        config.each { key, value ->
            variables.put(key, value)
        }

        variables.put("workflowName", workflow.name)
        variables.put("workflowLastUpdated", workflow.lastUpdated)
        variables.put("workflowOrganizations", organizations)
        variables.put("workflowLocation", workflow.location)
        variables.put("remotePath", workflow.location)
        variables.put("localPath", "${config.get("workspace.path")}/${workflow.name.replaceAll("\\W", "").toLowerCase()}")

        variables.put("jenkinsUrl", config.get("jenkins.url"))
        variables.put("jenkinsUser", config.get("jenkins.user"))
        variables.put("jenkinsPassword", config.get("jenkins.password"))

        variables.put("jiraUrl", config.get("jira.url"))
        variables.put("jiraUser", config.get("jira.user"))
        variables.put("jiraPassword", config.get("jira.password"))
        variables.put("jiraProject", config.get("jira.workflow"))
        variables.put("jiraIssueType", config.get("jira.issueType"))
        variables.put("businessKey", businessKey)
        variables.put('result', new ProcessResult())

        addSubscriptionVariables(variables, workflow)
    }

    def addSubscriptionVariables(variables, workflow){

        def subscriptionVariables = [:]
        workflow.tags.each{WorkflowTag tag ->
            tag.subscriptions.each {subscription ->
                subscription.variables.each {variable ->
                    if(subscriptionVariables[variable.name] == null){
                        subscriptionVariables[variable.name] = []
                    }
                    subscriptionVariables[variable.name] << variable.value
                }
            }
        }

        variables.subscriptionVariables = subscriptionVariables

    }
}
