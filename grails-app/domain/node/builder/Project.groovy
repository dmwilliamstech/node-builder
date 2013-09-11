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
    String bpmn
    String location
    ProjectType projectType
    Boolean active
    String processDefinitionKey

    /* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated


    static mapping = {
    }

    static constraints = {
        bpmn maxSize: 16000
        name blank: false
        name unique: true
        location validator: {value , object ->
            if(!value.matches(/((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\+\\u0024,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\\u0024,\w]+@)[A-Za-z0-9.-]+)((?:[\\/\:][\+~%\\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[.\!\\/\\w]*))?)/)){
                return 'url'
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
