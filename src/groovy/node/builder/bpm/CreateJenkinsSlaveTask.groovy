package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import com.offbytwo.jenkins.model.JobWithDetails
import node.builder.Manifest
import node.builder.Master
import node.builder.Utilities
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.fusesource.jansi.AnsiConsole
import org.joda.time.DateTime

class CreateJenkinsSlaveTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {

        log.info "Running jenkins job with expando"
        //do expando
        def jenkinsUrl = delegateExecution.getVariable("jenkinsUrl")
        JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl),
                delegateExecution.getVariable("jenkinsUser"), delegateExecution.getVariable("jenkinsPassword"))
        if(jenkins == null)
            throw new Exception("Unable to Connect to Jenkins server")

        def label = jenkins.getLabel("expando")
        def weCreatedAVM = false
        log.info "label vvvvv"
        log.info label.nodes
        if(label.nodes.size() == 0){
            log.info "Expanding build pool for ${delegateExecution.getVariable("jenkinsJobName")}"
            weCreatedAVM = true
            def manifest = Manifest.findByName("jenkins-agent")
            def master = Master.first()
            def utilities = new Utilities()
            delegateExecution.setVariable("manifest", utilities.serializeDomain(manifest))
            delegateExecution.setVariable("master", utilities.serializeDomain(master))
            Manifest.withTransaction {
                manifest.deployments.instances.removeAll()
                manifest.deployments.removeAll()
            }
            try{
                (new DeployTask()).execute(delegateExecution)
                log.info "Copied puppet node to master"
                (new ProvisionTask()).execute(delegateExecution)
                log.info "Provisioned Instance"
                while(jenkins.getLabel("expando")?.nodes?.size() == 0){
                    log.foo("Waiting for slave to check in")
                    sleep(1000)
                }
                log.foo("\nSlave has been detected\n")
            }catch (e){
                e.printStackTrace()
                throw e
            }
            log.info "Expansion complete"
        }

        //undo expando
        if(weCreatedAVM){

        }
    }
}
