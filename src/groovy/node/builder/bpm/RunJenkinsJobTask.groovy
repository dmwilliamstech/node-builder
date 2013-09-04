package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import com.offbytwo.jenkins.model.JobWithDetails
import node.builder.exceptions.MissingJenkinsJobException
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

        def details = getJobWithName(jenkins, jobName)



        delegateExecution.setVariable("result", buildJob(details, result))
        log.info "Building Job ${details.name} finished with result ${result.data.jenkinsBuild.result}"
    }

    def buildJob(job, result){

        def buildCount = job.builds.size()

        job.build()
        def details = job.details()

        log.info "Job ${job.name} has started"
        while(details.builds.size() == buildCount || details.lastBuild.details().isBuilding()){
            sleep(5000)
            details = job.details()
        }
        def build = details.builds.last().details()
        result.data.jenkinsBuild = getMapFromBuild(build)
        result.data.jenkinsBuild.url = "${job.url}${build.number}"
        result.data.jenkinsBuild.consoleUrl = "${job.url}${build.number}/console"


        return result
    }

    def getJobWithName(jenkins, jobName){
        Job job = jenkins.jobs[jobName]
        if(jobName == null || job == null)
            throw new MissingJenkinsJobException("Unable to find Jenkins job named ${jobName}", null)

        log.info "Found Job ${jobName}"
        JobWithDetails details = job.details()
        return details
    }
}
