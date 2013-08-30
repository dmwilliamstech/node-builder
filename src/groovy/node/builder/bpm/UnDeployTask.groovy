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
                Process process = "curl --cacert /usr/share/tomcat6/.ssh/ca_crt.pem --cert /usr/share/tomcat6/.ssh/stackbox_cert.novalocal.pem --key /usr/share/tomcat6/.ssh/stackbox_pk.novalocal.pem -k -X DELETE -H \"Accept: pson\" https://localhost:8140/production/certificate_status/${instance.name}.novalocal".execute()
                process.waitFor()
                if(process.exitValue() > 0){
                    log.error "Uh-oh there seems to be an issue ${process.err.text}"
                }else{
                    log.info "Cleaned puppet cert ${process.text}"
                }

            }catch(e){
                log.error "failed to remove certification for ${instance.name}.novalocal - ${e.getMessage()}"
                delegateExecution.setVariable("result", [error: [message: "Failed to unregister instance ${instance.name} ${e.getMessage()}"]])
                return
            }

        }
        delegateExecution.engineServices
        def manifest = Manifest.findByName(delegateExecution.getVariable("manifest").name)
        delegateExecution.setVariable("result", (new Utilities()).serializeDomain(manifest))
    }
}
