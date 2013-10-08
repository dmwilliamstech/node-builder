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
 * Project
 * A domain class describes the data object and it's mapping to the database
 */
class Project {
    static expose = 'project'

    /* Default (injected) attributes of GORM */
	Long	id

    String name
    String description
    String location
    ProjectType projectType
    Boolean active
    String processDefinitionKey
    String bpmn
    ProjectState state = ProjectState.OK
    String message = ""

    static hasMany = [organizations:String]

    /* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated


    static mapping = {
    }

    static constraints = {
        bpmn maxSize: 16000
        name blank: false
        name unique: true
        message nullable: true

        processDefinitionKey validator: {value, object ->
            if(value.contains("Please provide BPMN workflow")){
                return 'bpmn'
            }else{
                try{
                    ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine(object.name)
                    RepositoryService repositoryService = processEngine.getRepositoryService();

                    //TODO: JANKY
                    def file = File.createTempFile(value.replaceAll(/\W/, '_'),".bpmn20.xml")
                    if(file.exists())
                        file.delete()
                    file.write(object.bpmn)
                    org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment()
                            .addInputStream(file.path, new FileInputStream(file))
                            .deploy();
                    assert processEngine.repositoryService.createProcessDefinitionQuery().deploymentId(deployment.id).list().size() > 0
                    log.info("Loaded process definition for project ${object.name} with id (${deployment.id})")
                }catch(Exception e){
                    return "invalid"
                }
            }

        }

        location validator: {value ,object ->
            if(value.empty){
                return 'empty'
            }
            if(!LocationValidator.validateLocationForProjectType(value, object.projectType.name)){
                return 'connection'
            }
        }
    }

    /*
     * Methods of the Domain Class
     */
	@Override	// Override toString for a nicer / more descriptive UI
	public String toString() {
		return "${name}";
	}
}