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

import node.builder.bpm.DownloadSonarIssuesTask
import org.junit.After
import org.junit.Test

class DownloadSonarIssuesTaskTests extends BPMNTaskTestBase{

    def files = []

	@Test
    void shouldDownloadSonarIssues(){
        def sonarTask = new DownloadSonarIssuesTask()
        def variables = [sonarIssuesUrl: "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 2)
        sonarTask.execute(delegateExecution)

        assert variables.result.data.sonarIssuesFile != null
        assert new File(variables.result.data.sonarIssuesFile).exists()
        assert !variables.result.data.sonarIssues.isEmpty()
        assert variables.result.data.sonarIssues.maxResultsReached == false
        assert variables.sonarIssuesUrl == "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age&pageSize=-1&format=json"
        files <<  variables.result.data.sonarIssuesFile
    }


	@Test(expected = java.io.IOException)
    void shouldDetectBadIssuesUrl(){
        def sonarTask = new DownloadSonarIssuesTask()
        def variables = [sonarIssuesUrl: "http://broken:9000/api/issues/search?componentRoots=com.airgap:age"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 3, 1)
        sonarTask.execute(delegateExecution)
    }


    @Test
    void shouldDownloadAllIssues(){
        def sonarTask = new DownloadSonarIssuesTask()
        def variables = [sonarIssuesUrl: "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age&pageSize=10"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 2)
        sonarTask.execute(delegateExecution)

        assert variables.result.data.sonarIssuesFile != null
        assert new File(variables.result.data.sonarIssuesFile).exists()
        assert !variables.result.data.sonarIssues.isEmpty()
        assert variables.sonarIssuesUrl == "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age&pageSize=-1&format=json"

        files <<  variables.result.data.sonarIssuesFile
    }

    @Test
    void shouldPrettyPrintRawIssues(){
        def sonarTask = new DownloadSonarIssuesTask()
        def variables = [sonarIssuesUrl: "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age",
            sonarPrettyPrintIssues: true
        ]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 2)
        sonarTask.execute(delegateExecution)

        assert variables.result.data.sonarIssuesFile != null
        assert new File(variables.result.data.sonarIssuesFile).exists()
        assert variables.result.data.sonarIssuesPrettyPrintFile != null
        assert new File(variables.result.data.sonarIssuesPrettyPrintFile ).exists()
        assert new File(variables.result.data.sonarIssuesPrettyPrintPdfFile ).exists()
        assert !variables.result.data.sonarIssues.isEmpty()
        assert variables.result.data.sonarIssues.maxResultsReached == false
        assert variables.sonarIssuesUrl == "http://beaker:9000/api/issues/search?componentRoots=com.airgap:age&pageSize=-1&format=json"


        files <<  variables.result.data.sonarIssuesFile
        files <<  variables.result.data.sonarIssuesPrettyPrintFile
        files << variables.result.data.sonarIssuesPrettyPrintFile
    }

    @After
    void tearDown(){
        files.each{file ->
            new File(file).delete()
        }
    }
}
