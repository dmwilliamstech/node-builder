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
