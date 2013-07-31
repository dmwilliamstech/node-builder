package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import com.offbytwo.jenkins.model.JobWithDetails
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class RunJenkinsJobTask implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        log.info "Starting Jenkins build"
        ProcessResult result = delegateExecution.getVariable("result")?: new ProcessResult()
        JenkinsServer jenkins = new JenkinsServer(new URI(delegateExecution.getVariable("jenkinsUrl")),
                delegateExecution.getVariable("jenkinsUser"), delegateExecution.getVariable("jenkinsPassword"))
        if(jenkins == null)
            throw new Exception("Unable to Connect to Jenkins server")

        def jobName = delegateExecution.getVariable("jenkinsJobName")
        //TODO handle properly
        if(jobName == null)
            return
        Job job
        jenkins.jobs.each { name, j ->
            if(name == jobName){
                job = j
            }
        }

        if(job == null)
            throw new Exception("Unable to find Jenkins job named ${jobName}")

        log.info "Found Job ${jobName}"
        JobWithDetails details = job.details()
        def buildCount = details.builds.size()

        job.build()
        details = job.details()

        log.info "Job ${jobName} has started"
        while(details.builds.size() == buildCount || details.lastBuild.details().isBuilding()){
            sleep(5000)
            details = job.details()
        }
        def build = details.builds.last().details()
        result.data.jenkinsBuildResult = build.result
        result.data.jenkinsBuildDuration = build.duration
        result.data.jenkinsBuildId = build.id
        result.data.jenkinsBuildName = build.fullDisplayName
        result.data.jenkinsBuildParameters = build.parameters

        delegateExecution.setVariable("result", result)

        log.info "Building Job ${jobName} is finished"
    }
}
