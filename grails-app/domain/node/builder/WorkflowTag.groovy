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
 * WorkflowType
 * A domain class describes the data object and it's mapping to the database
 */
class WorkflowTag {
    /* Default (injected) attributes of GORM */
	Long	id

    String name

    /* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

    static belongsTo = [Workflow]
    static hasMany = [workflows: Workflow]

    static mapping = {
        workflows lazy: false
    }

    static constraints = {
    }

}