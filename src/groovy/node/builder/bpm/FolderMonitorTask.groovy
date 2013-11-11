package node.builder.bpm

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
class FolderMonitorTask extends MetricsTask{
    static final String FOLDER_MONITOR_FILE_DETECTION_LIST_KEY = 'folderMonitorFileDetectionList'
    static final String FOLDER_MONITOR_PATH_KEY = 'folderMonitorTargetLocation'
    static final String FOLDER_MONITOR_LAST_UPDATED_KEY = 'folderMonitorLastUpdated'
    static final String FOLDER_MONITOR_DETECTION_KEY = 'fileSystemDidChange'

    def processFiles(File target, result, DelegateExecution execution){
        def files = []

        def lastUpdated = execution.getVariable(FOLDER_MONITOR_LAST_UPDATED_KEY)
        if(!(lastUpdated instanceof Date)){
            lastUpdated = new Date(lastUpdated)
        }
        target.eachFileRecurse {File file ->
            def lastModified = new Date(file.lastModified())
            if(lastModified > lastUpdated && file.file){
                files << file.path
            }
        }
        result.message = "Successfully ran file monitor on target $target"
        result.data[FOLDER_MONITOR_DETECTION_KEY] = !files.empty
        result.data[FOLDER_MONITOR_FILE_DETECTION_LIST_KEY] = files
    }

    @Override
    void executeWithMetrics(DelegateExecution execution) {
        ProcessResult result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()

        def target = execution.getVariable(FOLDER_MONITOR_PATH_KEY)

        if(target instanceof String){
            target = new File(target)
        }

        if(!target?.exists() || !target?.isDirectory()){
            result.error.message = "Target $target must exist and be a directory".toString()
        }else{
            processFiles(target, result, execution)
        }
        execution.setVariable(ProcessResult.RESULT_KEY, result)
    }
}
