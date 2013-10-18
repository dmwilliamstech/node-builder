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

import com.mongodb.util.JSON
import org.activiti.engine.delegate.DelegateExecution

class DownloadSonarIssuesTask extends MetricsTask{
    private static final SONAR_ISSUES_URL_VAR = 'sonarIssuesUrl'

    void executeWithMetrics(DelegateExecution delegateExecution) throws Exception {
        def result = delegateExecution.getVariable("result") == null ? new ProcessResult() : delegateExecution.getVariable("result")

        def remoteUrl = delegateExecution.getVariable(SONAR_ISSUES_URL_VAR).toString()
        if(remoteUrl == null){
            throw new NullPointerException("Sonar Issues Url missing from variables object")
        }

        remoteUrl = remoteUrl.replaceAll(/\&?pageSize\=[\-\d]+/,'') + '&pageSize=-1'
        remoteUrl = new URL(remoteUrl + "&format=json").openConnection()

        def issuesFile = File.createTempFile("issues", ".json")
        issuesFile.withOutputStream { out ->
            out << remoteUrl.getInputStream()
        }

        result.data.sonarIssuesFile = issuesFile.path
        result.data.sonarIssues = JSON.parse(issuesFile.text)
        delegateExecution.setVariable(SONAR_ISSUES_URL_VAR, remoteUrl.URL.toString())
        delegateExecution.setVariable("result", result)
    }
}
