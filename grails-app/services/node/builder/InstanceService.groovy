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

import grails.converters.JSON

/**
 * InstanceService
 * A service class encapsulates the core business logic of a Grails application
 */
class InstanceService {

    static transactional = true

    def loadInstances(connection){
        log.info("Loading instances")
        def instances = connection.instances()
        def savedInstances = Instance.all
        instances.each { server ->


            savedInstances = savedInstances.findAll({ i ->
                i.instanceId != server.id
            })

            def instanceInstance = Instance.findByInstanceId(server.id)
            if(instanceInstance){
                instanceInstance.name = server.name
                instanceInstance.status = server.status
                instanceInstance.hostId = server.hostId
                instanceInstance.privateIP = server?.addresses?.private?.getAt(0)?.addr
                instanceInstance.keyName = server.key_name.toString()
                instanceInstance.flavor = Flavor.findByFlavorId(server.flavor.id)
                instanceInstance.userId = server.user_id
                instanceInstance.tenantId = server.tenant_id
                instanceInstance.progress = server.progress
                instanceInstance.configDrive = server.config_drive
                instanceInstance.metadata = (server.metadata as JSON).toString()
                instanceInstance.image = Image.findByImageId(server.image.id)
            }else{
                instanceInstance = new Instance(
                    name: server.name,
                    status: server.status,
                    hostId: server.hostId,
                    privateIP: server?.addresses?.private?.getAt(0)?.addr,
                    keyName: server.key_name.toString(),
                    flavor: Flavor.findByFlavorId(server.flavor.id),
                    instanceId: server.id,
                    userId: server.user_id,
                    tenantId: server.tenant_id,
                    progress: server.progress,
                    configDrive: server.config_drive,
                    metadata: (server.metadata as JSON).toString(),
                    image: Image.findByImageId(server.image.id)
            )
            instanceInstance.save(failOnError: true)
            }

        }

        savedInstances.each{ Instance instance ->
            log.info("Deleting instance ${instance.name} (${instance.instanceId})")
            instance.deployment?.removeFromInstances(instance)
            instance.delete()
        }
    }


}
