package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution

import java.util.regex.Matcher
import java.util.regex.Pattern

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
class ClamAVTask extends ShellTask{
    static final String FILE_LIST_KEY = 'clamAVScanList'
    static final String SUMMARY_STRING = '----------- SCAN SUMMARY -----------'

    def processOutput(String output){
        def data = [files:[], summaries: [], output: output]

        output.split(SUMMARY_STRING).eachWithIndex{ half, i ->
            half.split('\n').each{line ->
                def values = line.split(':')
                if(values.size() > 1)
                    if(i == 0){
                        data.files << [path: values[0].trim(), status: values[1].trim()]
                    }else{
                        data.summaries << [name: values[0].trim(), value: values[1].trim()]
                    }
            }
        }

        return data
    }

    @Override
    void executeWithMetrics(DelegateExecution execution) {

        ProcessResult result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()
        def output = File.createTempFile('clamscan','.txt')
        def error = File.createTempFile('clamscanerror','.txt')
        def scanListFile = File.createTempFile('scanList', '.txt')
        def filesToScan = execution.getVariable(FILE_LIST_KEY)
        try{
            if(filesToScan instanceof List){
                filesToScan = filesToScan.join('\n')
            } else {
                filesToScan = filesToScan.toString().replaceAll(/\,/,'\n')
            }

            scanListFile.write(filesToScan)

            try{
                runCommand("clamscan -f ${scanListFile.path}", System.getProperty("java.io.tmpdir"), output, error)
            } catch(e){
                result.error.message = e.getMessage()
                result.message = e.getMessage()
            }
            result.message = "Scan of file(s) (${filesToScan.replaceAll('\n', ', ')}) is complete"
            result.data.clamAVScan = processOutput(output.text)
            execution.setVariable(ProcessResult.RESULT_KEY, result)
        }finally {
            !output.exists()?:output.delete()
            !error.exists()?:error.delete()
            !scanListFile.exists()?:scanListFile.delete()
        }
    }
}
