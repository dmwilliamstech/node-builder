package node.builder.bpm

import grails.converters.JSON
import node.builder.Deployment
import node.builder.Image
import node.builder.Instance
import node.builder.Manifest
import node.builder.OpenStackConnection
import node.builder.Utilities
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class ProvisionTask implements JavaDelegate{
    public void execute(DelegateExecution delegateExecution) throws Exception {
        def manifest = Manifest.findByName(delegateExecution.getVariable("manifest").name)
        def instances = []
        for(instance in manifest.manifest.instances){

            def instanceName = instance.name.replaceAll(/\s/, '-')
            if(Instance.findByName(instanceName)){
                delegateExecution.setVariable("error", [error: [message: "Instance ${instanceName} already exists please update to continue"]])
                return
            }
            def instanceData =  OpenStackConnection.connection.launch(instance.flavorId, manifest.manifest.imageId, instanceName)
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

        def deployment = new Deployment(manifest: manifest, instances: instances)
        deployment.save(failOnError: true)

        delegateExecution.engineServices
        delegateExecution.setVariable("result", (new Utilities()).serializeDomain(manifest))
    }
}
