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

import grails.converters.JSON
import node.builder.*
import node.builder.virt.OpenStackConnection
import node.builder.virt.OpenStackFlavors
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate

public class ProvisionTask implements JavaDelegate{
    public void execute(DelegateExecution delegateExecution) throws Exception {
        def manifest = Manifest.findByName(delegateExecution.getVariable("manifest").name)
        def instances = []

        def names = []
        manifest.manifest.instances.each{instance -> names.add(instance.name)}

        def instanceDomains = Instance.findAllByNameInList(names)

        Deployment deployment
        if(!instanceDomains.empty){
            deployment = instanceDomains.first().deployment
        }

        for(instance in manifest.manifest.instances){

            def instanceName = instance.name.replaceAll(/\s/, '-')

            if(!instanceDomains.find { Instance id ->  (instanceName == id.name) }){
                def instanceData
                try{
                    instanceData = OpenStackConnection.connection.launch(OpenStackFlavors.flavorForName(instance.flavorId), manifest.manifest.imageId, instanceName)
                }catch(e){
                    println(e.message)
                    e.printStackTrace()
                    throw e
                }
                def server = instanceData.server
                if(server != null){
                    def instanceDomain = new Instance(
                            name: server.name,
                            status: server.status,
                            hostId: server.hostId,
                            privateIP: (server.addresses?.private?.getAt(0)?.addr ?: "not set"),
                            keyName: server.key_name.toString(),
                            flavorId: server.flavor.id,
                            instanceId: server.id,
                            userId: server.user_id,
                            tenantId: server.tenant_id,
                            progress: server.progress,
                            configDrive: server.config_drive ?: "not set",
                            metadata: (server.metadata as JSON).toString(),
                            image: Image.findByImageId(server.image.id)
                    )
                    instanceDomain.save(failOnError: true)
                    instances.add(instanceDomain)
                }else{
                    delegateExecution.setVariable("result", instanceData)
                    return
                }
            }

        }

        addInstancesToDeploymentAndManifest(deployment, instances, manifest)

        delegateExecution.engineServices
        delegateExecution.setVariable("result", (new Utilities()).serializeDomain(manifest))
    }


    def addInstancesToDeploymentAndManifest(deployment, instances, manifest){
        if(deployment == null){
            deployment = new Deployment(manifest: manifest, instances: instances)
            deployment.save(failOnError: true, flush: true)

        }else{
            deployment.instances.addAll(instances)
            deployment.save(failOnError: true, flush: true)
        }


        instances.each{instance ->
            instance.deployment = deployment
            instance.save()
        }
    }
}
