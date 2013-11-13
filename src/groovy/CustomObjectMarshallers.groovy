import grails.converters.JSON
import node.builder.SubscriptionVariable
import node.builder.WorkflowTag

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
class CustomObjectMarshallers {

    static def registerCustomObjectMarshallers(){
        JSON.registerObjectMarshaller(WorkflowTag) {
            def json = [:]
            json.name = it.name
            json.id = it.id
            json.workflows = it.workflows?.collect{[
                    id:it.id,
                    name: it.name,
                    description : it.description
            ]}
            json.lastUpdated = it.lastUpdated
            json.dateCreated = it.dateCreated
            return json
        }

        JSON.registerObjectMarshaller(SubscriptionVariable) {
            def json = [:]
            json.name = it.name
            json.id = it.id
            json.value = it.value

            json.lastUpdated = it.lastUpdated
            json.dateCreated = it.dateCreated
            return json
        }
    }

}
