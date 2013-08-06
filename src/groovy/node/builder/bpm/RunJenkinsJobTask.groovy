package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import com.offbytwo.jenkins.model.JobWithDetails
import grails.converters.JSON
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class RunJenkinsJobTask extends JenkinsJobTask implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        log.info "Starting Jenkins build"
        ProcessResult result = delegateExecution.getVariable("result")?: new ProcessResult()
        def jenkinsUrl = delegateExecution.getVariable("jenkinsUrl")
        JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl),
                delegateExecution.getVariable("jenkinsUser"), delegateExecution.getVariable("jenkinsPassword"))
        if(jenkins == null)
            throw new Exception("Unable to Connect to Jenkins server")

        def jobName = delegateExecution.getVariable("jenkinsJobName")
        //TODO handle properly
        if(jobName == null)
            return
        Job job = jenkins.jobs[jobName]
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
        result.data.jenkinsBuild = getMapFromBuild(build)
        result.data.jenkinsBuild.url = "${jenkinsUrl}/job/${jobName}/${build.number}"
        result.data.jenkinsBuild.consoleUrl = "${jenkinsUrl}/job/${jobName}/${build.number}/console"

        delegateExecution.setVariable("result", result)

        log.info "Building Job ${jobName} finished with result ${result.data.jenkinsBuild.result}"
    }
}
