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
import node.builder.bpm.DownloadSonarIssuesTask
import node.builder.bpm.RunJenkinsJobTask
import org.junit.Ignore
import org.junit.Test

class DownloadSonarIssuesTaskTests extends BPMNTaskTestBase{

	@Test
    void shouldDownloadSonarIssues(){
        def sonarTask = new DownloadSonarIssuesTask()
        def variables = [sonarIssuesUrl: "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 4, 1)
        sonarTask.execute(delegateExecution)

        assert variables.result.data.sonarIssuesFile != null
        assert new File(variables.result.data.sonarIssuesFile).exists()
        assert !variables.result.data.sonarIssues.isEmpty()
        assert variables.result.data.sonarIssues.maxResultsReached == false
    }


	@Test(expected = java.net.UnknownHostException)
    void shouldDetectBadIssuesUrl(){
        def sonarTask = new DownloadSonarIssuesTask()
        def variables = [sonarIssuesUrl: "http://broken:9000/api/issues/search?componentRoots=com.airgap:age"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 2, 1)
        sonarTask.execute(delegateExecution)
    }
}
