package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.apache.commons.io.FilenameUtils

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
class NexusStorageTask extends ShellTask{
    static final String NEXUS_REPO_FILE_LIST_KEY = 'nexusStorageFileList'
    static final String NEXUS_REPO_URL_KEY = 'nexusStorageRepoUrl'
    static final String NEXUS_REPO_VERSION_KEY = 'nexusStorageRepoVersion'
    static final String NEXUS_REPO_USER_KEY = 'nexusStorageRepoUser'
    static final String NEXUS_REPO_PASSWORD_KEY = 'nexusStorageRepoPassword'

    def processFiles(List fileList, result, output, error, DelegateExecution execution){
        def project = execution.getVariable("projectName")
        def version = execution.getVariable(NEXUS_REPO_VERSION_KEY)
        def url =     execution.getVariable( NEXUS_REPO_URL_KEY )
        def urls = []
        url = "${url}/projects/${project}/${version}/"
        result.message = ""
        fileList.each {filePath ->
            try{
                def filename = FilenameUtils.getName(filePath)
                url + filename
                runCommand("curl -T ${filePath} ${url + filename} -u ${execution.getVariable(NEXUS_REPO_USER_KEY)}:${execution.getVariable(NEXUS_REPO_PASSWORD_KEY)}",
                        './',
                        output.path,
                        error.path)
                urls << [filename: filename, url: url + filename, title: filename]
                result.message += "Uploaded $filename to ${url + filename}\n"
            } catch(e){
               result.error.message = e.getMessage()
               result.message = e.getMessage()
            }

            if(result.data.artifactUrls == null)
                result.data.artifactUrls = []
        }

        result.data.artifactUrls.addAll(urls)
        result.data.nexusStorageUrls = urls
    }

    @Override
    void executeWithMetrics(DelegateExecution execution) {

        ProcessResult result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()
        def output = File.createTempFile('nexusstorage','.txt')
        def error = File.createTempFile('nexusstorage','.txt')

        def filesToScan = execution.getVariable(NEXUS_REPO_FILE_LIST_KEY)
        try{
            if(filesToScan instanceof String){
                filesToScan = filesToScan.split(',').toList()
            }

            processFiles(filesToScan, result, output, error, execution)
            execution.setVariable(ProcessResult.RESULT_KEY, result)

        }finally {
            !output.exists()?:output.delete()
            !error.exists()?:error.delete()
        }
    }
}
