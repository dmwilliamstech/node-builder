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
                    loadProcessDefinitions(params.name, params.bpmn, params.processDefinitionKey)
                }
            }
            afterView = { Exception e ->

            }
        }
    }

    def loadProcessDefinitions(projectName, String bpmn, String processDefinitionKey){
        ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine(projectName)
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //TODO: JANKY
        def file = File.createTempFile(processDefinitionKey.replaceAll(/\W/, '_'),".bpmn20.xml")
        if(file.exists())
            file.delete()
        file.write(bpmn)
        org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment()
                    .addInputStream(file.path, new FileInputStream(file))
                    .deploy();
        assert processEngine.repositoryService.createProcessDefinitionQuery().deploymentId(deployment.id).list().size() > 0
        log.info("Loaded process definition for project ${projectName} with id (${deployment.id})")

    }
}
