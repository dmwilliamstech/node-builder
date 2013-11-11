package node.builder.bpm

import node.builder.SCPFileCopier
import org.activiti.engine.delegate.DelegateExecution

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
class SCPFileCopierTask extends MetricsTask{
    static final String SCP_SRC_FILE_LIST_KEY = 'scpFileList'
    static final String SCP_KEY_PATH_KEY = 'scpKeyPath'
    static final String SCP_DEST_FILE_PATH_KEY = 'scpDestinationPath'
    static final String SCP_HOSTNAME_KEY = 'scpHostname'
    static final String SCP_USER_KEY = 'scpUser'


    def uploadFile(file, hostname, username, keyPath, destPath){
        def scpFileCopier = new SCPFileCopier()
        scpFileCopier.copyTo(new File(file),
                hostname,
                new File(destPath),
                username,
                new File(keyPath)
        )
    }

    def processFiles(List fileList, result, DelegateExecution execution){

        def hostname = execution.getVariable(SCP_HOSTNAME_KEY)
        def username = execution.getVariable(SCP_USER_KEY)
        def keyPath = execution.getVariable(SCP_KEY_PATH_KEY)
        def destPath = execution.getVariable(SCP_DEST_FILE_PATH_KEY)
        fileList.each {file ->
            try{
                uploadFile(file, hostname, username, keyPath, destPath)
            }catch(e){
                result.error.message = e.message
                result.message = e.message
                return
            }
            result.message += "\nFile $file copied to $username@$hostname:$destPath"
        }

    }

    @Override
    void executeWithMetrics(DelegateExecution execution) {
        ProcessResult result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()

        def filesToCopy = execution.getVariable(SCP_SRC_FILE_LIST_KEY)

        if(filesToCopy instanceof String){
            filesToCopy = filesToCopy.split(',').toList()
        }

        processFiles(filesToCopy, result, execution)
        execution.setVariable(ProcessResult.RESULT_KEY, result)
    }
}
