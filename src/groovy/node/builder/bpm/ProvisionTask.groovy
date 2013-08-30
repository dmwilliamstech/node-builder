package node.builder.bpm

import grails.converters.JSON
import node.builder.*
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate

public class ProvisionTask implements JavaDelegate{
    public void execute(DelegateExecution delegateExecution) throws Exception {
        def manifest = Manifest.findByName(delegateExecution.getVariable("manifest").name)
        def instances = []

        def names = []
        manifest.manifest.instances.each{instance -> names.add(instance.name)}

        def criteria = Instance.createCriteria()
        def instanceDomains = criteria.list {
            'in'("name", names)
        }

        Deployment deployment
        if(!instanceDomains.empty){
            deployment = instanceDomains.first().deployment
        }

        for(instance in manifest.manifest.instances){

            def instanceName = instance.name.replaceAll(/\s/, '-')

            if(!instanceDomains.find { Instance id ->  (instanceName == id.name) }){
                def instanceData
                try{
                    instanceData = OpenStackConnection.connection.launch(instance.flavorId, manifest.manifest.imageId, instanceName)
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
            deployment.save(failOnError: true)

        }else{
            deployment.instances.addAll(instances)
            deployment.save(failOnError: true)
        }

        instances.each{instance ->
            instance.deployment = deployment
            instance.save()
        }
    }
}
