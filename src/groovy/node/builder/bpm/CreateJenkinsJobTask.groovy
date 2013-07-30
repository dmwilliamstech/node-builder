package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class CreateJenkinsJobTask implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        JenkinsServer jenkins = new JenkinsServer(new URI(delegateExecution.getVariable("jenkinsUrl")),
                delegateExecution.getVariable("jenkinsUser"), delegateExecution.getVariable("jenkinsPassword"))
        if(jenkins == null)
            throw "Unable to Connect to Jenkins server"
        String sourceXml = jenkins.getJobXml("test");
        println sourceXml
        def jobName = delegateExecution.getVariable("jenkinsJobName")
        //TODO handle properly
        if(jobName == null)
            return
        def jobXml  = delegateExecution.getVariable("jenkinsJobXml")



        jenkins.createJob(jobName, jobXml)
    }
}
