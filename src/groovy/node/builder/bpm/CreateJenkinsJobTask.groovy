package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class CreateJenkinsJobTask extends JenkinsJobTask implements JavaDelegate {
    void execute(DelegateExecution delegateExecution) throws Exception {
        ProcessResult result = delegateExecution.getVariable("result")?: new ProcessResult()

        JenkinsServer jenkins = new JenkinsServer(new URI(delegateExecution.getVariable("jenkinsUrl")),
                delegateExecution.getVariable("jenkinsUser"), delegateExecution.getVariable("jenkinsPassword"))
        if(jenkins == null)
            throw "Unable to Connect to Jenkins server"

        String jobName = delegateExecution.getVariable("jenkinsJobName")
        if(jobName == null){
            throw new Exception("No Job name provided")
        }

        def jobXml  = delegateExecution.getVariable("jenkinsJobXml")
        if(jobXml == null)
            throw new Exception("No Job XML provided")

        log.info("Creating ${jobName} with ${jobXml}")
        jenkins.createJob(jobName, jobXml)

        Job job = null
        [0,1,2,3,4,5].find{ number ->
            job = jenkins.jobs[jobName]?.details()
            if(job != null){
                log.info("Found ${job.name} in jenkins")
                return true
            }
            sleep(1000)
            return false
        }

        if(job == null)
            throw new Exception("Job not found in Jenkins after creation")

        result.data.jenkinsJob = getMapFromJob(job)
        delegateExecution.setVariable("result", result)
    }


}
