package node.builder.bpm

import node.builder.Manifest
import node.builder.Utilities
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class UnDeployTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {
        def deployment = delegateExecution.getVariable("deployment")
        for(instance in deployment.instances){
            try{
//                Process p = "puppet cert clean #{instance.name}.novalocal".execute()
                "curl --cacert ~/.ssh/ca_crt.pem --cert ~/.ssh/stackbox_cert.novalocal.pem --key ~/.ssh/stackbox_pk.novalocal.pem -k -X DELETE -H \"Accept: pson\" https://localhost:8140/production/certificate_status/#{instance.name}.novalocal".execute()
            }catch(e){
                delegateExecution.setVariable("result", [error: [message: "Failed to unregister instance ${instance.name} ${e.getMessage()}"]])
                return
            }

        }
        delegateExecution.engineServices
        def manifest = Manifest.findByName(delegateExecution.getVariable("manifest").name)
        delegateExecution.setVariable("result", (new Utilities()).serializeDomain(manifest))
    }
}
