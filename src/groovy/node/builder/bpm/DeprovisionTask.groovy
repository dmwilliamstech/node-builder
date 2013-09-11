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


import node.builder.Manifest
import node.builder.OpenStackConnection
import node.builder.Utilities
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class DeprovisionTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {

        def manifest = Manifest.findByName(delegateExecution.getVariable("manifest").name)
        def deployment = delegateExecution.getVariable("deployment")

        for(instance in deployment.instances){
            try{
                if(!OpenStackConnection.connection.delete(instance.instanceId)){
                    throw new Exception("Failed to delete instance ${instance.name}")
                }
            }catch(e){
                log.error instance
                throw new Exception("Failed to delete instance ${instance.name} ${e.getMessage()}", e)
            }

        }
        delegateExecution.engineServices
        delegateExecution.setVariable("result", (new Utilities()).serializeDomain(manifest))
    }
}
