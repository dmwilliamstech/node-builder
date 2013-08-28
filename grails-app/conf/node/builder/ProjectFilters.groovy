package node.builder

import node.builder.bpm.ProcessEngineFactory
import org.activiti.engine.ProcessEngine
import org.activiti.engine.RepositoryService

/**
 * ProjectFilters
 * A filters class is used to execute code before and after a controller action is executed and also after a view is rendered
 */
class ProjectFilters {

    def filters = {
        create(controller:'project', action:'save') {
            before = {

            }
            after = { Map model ->
                if(model == null){
                    loadProcessDefinitions(params.name, params.bpmn)
                }
            }
            afterView = { Exception e ->

            }
        }
    }

    def loadProcessDefinitions(projectName, bpmn){
        ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine("loaded")
        RepositoryService repositoryService = processEngine.getRepositoryService();


        org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment()
                    .addString(projectName, bpmn)
                    .deploy();

        log.info("Loaded process definition for project ${projectName} with id (${deployment.id})")

    }
}
