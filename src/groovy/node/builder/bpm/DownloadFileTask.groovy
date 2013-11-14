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

class DownloadFileTask extends MetricsTask{
    static final String FILE_URL_KEY = 'downloadFileUrl'
    static final String FILE_USER_KEY = 'downloadFileUsername'
    static final String FILE_PASSWORD_KEY = 'downloadFilePassword'
    static final String FILE_OUTPUT_PATH = 'downloadFileOutputPath'

    def streamFile(outputFile, remoteUrl, delegateExecution){
        def url = new URL(remoteUrl).openConnection()
        if(delegateExecution.getVariable(FILE_USER_KEY) != null){
            def remoteAuth = "Basic " + "${delegateExecution.getVariable(FILE_USER_KEY)}:${delegateExecution.getVariable(FILE_PASSWORD_KEY)}".getBytes().encodeBase64().toString()
            url.setRequestProperty("Authorization", remoteAuth);
        }

        outputFile.withOutputStream { out ->
            out << url.getInputStream()
        }
    }

    void executeWithMetrics(DelegateExecution delegateExecution) throws Exception {
        def result = delegateExecution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()
        def remoteUrl = delegateExecution.getVariable(FILE_URL_KEY)

        if(remoteUrl == null){
            throw new NullPointerException("File URL is missing from result object")
        }


        def outputPath = new File(delegateExecution.getVariable(FILE_OUTPUT_PATH))

        if(outputPath.directory){
            outputPath = new File("${delegateExecution.getVariable(FILE_OUTPUT_PATH)}/${url.getURL().file}")
        }

        streamFile(outputPath, remoteUrl, delegateExecution)

        result.data.downloadedFile = outputPath.path
        delegateExecution.setVariable("result", result)
    }
}
