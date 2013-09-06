package node.builder

import com.offbytwo.jenkins.model.BuildResult
import groovy.mock.interceptor.MockFor
import node.builder.bpm.DownloadJenkinsJobConsoleTask
import node.builder.bpm.RunJenkinsJobTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.Test


class DownloadJenkinsJobConsoleTaskTests extends BPMNTaskTestBase{

    @Test
    void shouldRunTestJobAndDownloadConsole(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 6, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.result == BuildResult.SUCCESS

        assert variables.result.data.jenkinsBuild.consoleUrl.contains("http://stackbox:9999/job/test/")

        delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask = new DownloadJenkinsJobConsoleTask()
        jenkinsTask.execute(delegateExecution)

        assert variables.result.data.jenkinsConsoleFile != null
        assert new File(variables.result.data.jenkinsConsoleFile).exists()
    }

    @Test(expected = NullPointerException)
    void shouldDetectMissingConsoleUrl(){
        def jenkinsTask = new DownloadJenkinsJobConsoleTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask.execute(delegateExecution)
    }
}
