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

import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * ProjectService
 * A service class encapsulates the core business logic of a Grails application
 */
class ProjectService {

    static transactional = true

    static def pool = Executors.newCachedThreadPool()
    static Map futures = [:]


    def createWithoutSaving(params, organizations){
        def projectInstance = new Project(params)

        projectInstance.organizations = organizations
        return projectInstance
    }


    def findAllByOrganizations(organizations, params){
        if(organizations == null || organizations.empty)
            return []
        def projects = Project.executeQuery(
                'from Project p where :organizations in elements(p.organizations)',
                [organizations: organizations], params)
        return projects
    }

    def getByOrganizations(id, organizations){
        if(organizations == null || organizations.empty)
            return null
        def project = Project.executeQuery(
                'from Project p where p.id=:id and :organizations in elements(p.organizations)',
                [id: Long.parseLong(id), organizations: organizations])

        return project.empty ? null : project.first()
    }

    def filesFromResult(ProcessResult result){
        try{
            def files = []
            if(result?.data == null)
                return files

            result.data.each{ key, value ->
                if(key.toString().toLowerCase().contains('file'))
                    files << [path: value, name: new File(value).name]
            }

            return files
        }catch(e){
            e.printStackTrace()
        }
    }

    def run(project) {
        def message = ""

            if(futures.get(project.name) != null && !futures.get(project.name).isDone())
                return [message: "Project already running", project: project]
            project.state = ProjectState.RUNNING
            project.message = "Running process ${project.processDefinitionKey} on project ${project.name}"
            project.save(validate: false)

            futures.remove(project.name)
            futures.put(project.name, pool.submit(new Callable<ProcessResult>() {
                    public ProcessResult call() {
                        def businessKey = "${project.name}-${java.util.UUID.randomUUID()}"
                        def start = System.currentTimeMillis()
                        def result
                        try{
                            def processEngine = ProcessEngineFactory.defaultProcessEngine(project.name)

                            log.metric(MetricEvents.START, MetricGroups.WORKFLOW, "", businessKey, project.name, "")

                            def variables = new HashMap();

                            def config =  Config.getGlobalConfig()
                            config.each { key, value ->
                                variables.put(key, value)
                            }

                            variables.put("projectName", project.name)
                            variables.put("remotePath", project.location)
                            variables.put("localPath", "${config.get("workspace.path")}/${project.name.replaceAll("\\W", "").toLowerCase()}")

                            variables.put("jenkinsUrl", config.get("jenkins.url"))
                            variables.put("jenkinsUser",config.get("jenkins.user"))
                            variables.put("jenkinsPassword", config.get("jenkins.password"))

                            variables.put("jiraUrl", config.get("jira.url"))
                            variables.put("jiraUser", config.get("jira.user"))
                            variables.put("jiraPassword", config.get("jira.password"))
                            variables.put("jiraProject", config.get("jira.project"))
                            variables.put("jiraIssueType", config.get("jira.issueType"))
                            variables.put("businessKey", businessKey)
                            variables.put("projectName",  project.name)

                            log.info "Running process ${project.processDefinitionKey} on project ${project.name}"
                            result = ProcessEngineFactory.runProcessWithBusinessKeyAndVariables(processEngine, project.processDefinitionKey, businessKey, variables)
                            log.info "Process ${project.processDefinitionKey} on project ${project.name} finished"

                            project.state = ProjectState.OK
                            project.message = "Process ${project.processDefinitionKey} on project ${project.name} finished - ${result?.message}"
                        }catch(ActivitiObjectNotFoundException e){
                            log.error(e.getMessage())
                            e.printStackTrace()
                            project.state = ProjectState.WARNING
                            project.message = "Unable to detect state of project, please add a receive task to your workflow"
                            result = new ProcessResult(project.message, [:])
                        }catch(e){
                            log.error(e.getMessage())
                            e.printStackTrace()
                            project.state = ProjectState.ERROR
                            project.message = e.getMessage()
                            result = new ProcessResult(project.message, [:])
                        }finally{
                            result?.data?.status = project?.state
                            result?.data?.artifactFiles = filesFromResult(result)

                            def duration = -1
                            if(result?.data?.repositoryDidChange){
                                duration = (System.currentTimeMillis() - start)
                            }

                            log.metric(MetricEvents.FINISH, MetricGroups.WORKFLOW, project.message, businessKey,  project.name, "", result, duration)

                            log.info("Saving project object with state $project.state")
                            project.save(validate: false)
                        }
                        return new ProcessResult(project.message, project)
                    }
                }
            ));
        return [message: project.message, project: project]
    }


}
