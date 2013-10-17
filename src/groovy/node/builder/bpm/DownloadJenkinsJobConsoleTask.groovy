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

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate


class DownloadJenkinsJobConsoleTask extends MetricsTask{
    void executeWithMetrics(DelegateExecution delegateExecution) throws Exception {
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

        def consoleFile = File.createTempFile("console", ".txt")
        consoleFile.withOutputStream { out ->
            out << url.getInputStream()
        }

        result.data.jenkinsConsoleFile = consoleFile.path
        delegateExecution.setVariable("result", result)
    }
}
