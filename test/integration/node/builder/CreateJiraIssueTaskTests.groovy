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

import node.builder.bpm.CreateJiraIssueTask
import org.junit.Before
import org.junit.Ignore

class CreateJiraIssueTaskTests extends BPMNTaskTestBase{
    final shouldFail = new GroovyTestCase().&shouldFail

    @Before
    void setup(){

    }

    @Ignore
    void testCreateIssue(){
        def variables = [jiraUrl: "http://stackbox:8181/",
                jiraUser:"admin",
                jiraPassword:"foobar99",
                jiraProject: "TST",
                jiraIssueType: 1l,
                jiraIssueSummary: "It's all broken!",
                jiraIssueDescription: "You really stuffed it up, didn't you?"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, variables.size(), 1)
        def createJiraIssueTask = new CreateJiraIssueTask()

        createJiraIssueTask.execute(delegateExecution)

        assert variables.result.data.jiraIssueKey.matches(/TST-\d+/)
    }
}
