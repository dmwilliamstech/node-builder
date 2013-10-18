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

package node.builder

import com.offbytwo.jenkins.model.BuildResult
import node.builder.bpm.DownloadJenkinsJobConsoleTask
import node.builder.bpm.RunJenkinsJobTask
import org.junit.Ignore
import org.junit.Test

class DownloadJenkinsJobConsoleTaskTests extends BPMNTaskTestBase{

    @Ignore
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

    @Ignore
	@Test(expected = NullPointerException)
    void shouldDetectMissingConsoleUrl(){
        def jenkinsTask = new DownloadJenkinsJobConsoleTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        jenkinsTask.execute(delegateExecution)
    }
}
