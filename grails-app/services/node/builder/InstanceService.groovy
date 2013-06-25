package node.builder

import grails.converters.JSON

/**
 * InstanceService
 * A service class encapsulates the core business logic of a Grails application
 */
class InstanceService {

    static transactional = true

    def loadInstances(connection){
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
                instanceInstance.flavorId = server.flavor.id
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
                    flavorId: server.flavor.id,
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

        Instance.deleteAll(savedInstances)
    }


}
