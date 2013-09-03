package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class DownloadJenkinsJobConsoleTask implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        def result = delegateExecution.getVariable("result")
        def remoteUrl = result?.data.jenkinsBuild.consoleUrl

        if(remoteUrl == null){
            throw new NullPointerException("Console URL is missing from result object")
        }

        def url = new URL(remoteUrl + "Text").openConnection()
        if(delegateExecution.getVariable("jenkinsUser") != null){
            def remoteAuth = "Basic " + "${delegateExecution.getVariable("jenkinsUser")}:${delegateExecution.getVariable("jenkinsPassword")}".getBytes().encodeBase64().toString()
            url.setRequestProperty("Authorization", remoteAuth);
        }

        def consoleFile = File.createTempFile("console", "txt")
        consoleFile.withOutputStream { out ->
            out << url.getInputStream()
        }

        result.data.jenkinsConsoleFile = consoleFile.path
        delegateExecution.setVariable("result", result)
    }
}
