
package node.builder

import com.offbytwo.jenkins.model.BuildResult
import node.builder.bpm.DeleteJenkinsJobTask
import node.builder.bpm.RunJenkinsJobTask
import node.builder.exceptions.MissingJenkinsJobException
import org.junit.After
import org.junit.Ignore
import org.junit.Test

class RunJenkinsJobTaskTests extends BPMNTaskTestBase{

    @Ignore
	@Test(expected = MissingJenkinsJobException)
    void shouldDetectMissingJob(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"notreal"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask.execute(delegateExecution)
    }

    @Ignore
	@Test
    void shouldRunTestJob(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 6, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.result == BuildResult.SUCCESS
    }

    @Ignore
    @Test
    void shouldRunTestJobWithParameters(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99",
                jenkinsJobName:"testWithParameters",
                jenkinsParameters:["PARAM_1":"test at first", "PARAM_2": "test some more"]]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 6, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.result == BuildResult.SUCCESS
    }

    @Ignore
	@Test
    void shouldHaveBuildUrlsJob(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.url.contains("http://stackbox:9999/job/test/")
        assert variables.result.data.jenkinsBuild.consoleUrl.contains("http://stackbox:9999/job/test/")
    }

    @Ignore
	@Test
    void shouldRunFailJob(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"fail"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.result == BuildResult.FAILURE
    }
}
