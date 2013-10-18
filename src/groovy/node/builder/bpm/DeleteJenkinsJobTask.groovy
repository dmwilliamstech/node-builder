/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package node.builder.bpm

import com.offbytwo.jenkins.JenkinsServer
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate

class DeleteJenkinsJobTask extends JenkinsJobTask implements JavaDelegate {
    void executeWithMetrics(DelegateExecution delegateExecution) throws Exception {
        ProcessResult result = delegateExecution.getVariable("result")?: new ProcessResult()

        JenkinsServer jenkins = new JenkinsServer(new URI(delegateExecution.getVariable("jenkinsUrl")),
                delegateExecution.getVariable("jenkinsUser"), delegateExecution.getVariable("jenkinsPassword"))
        if(jenkins == null)
            throw "Unable to Connect to Jenkins server"

        String jobName = delegateExecution.getVariable("jenkinsJobName")
        if(jobName == null){
            throw new Exception("No Job name provided")
        }

        def message = "Deleting jobs: \n"
        log.info("Finding jobs matching ${jobName}")
        def jobs = []
        jenkins.getJobs().each{ name, job ->
            if(name ==~ jobName){
                message += "${name} \n"
                jobs << job
            }
        }

        jobs.each {job ->
            log.info("Found job named ${job.name}, deleting")
            try{
                job.doDelete()
            }catch(SocketException){
                log.warn "Received Socket Exception when deleting job ${job.name}"
            }
        }

        result.message = message
        delegateExecution.setVariable("result", result)
    }
}
