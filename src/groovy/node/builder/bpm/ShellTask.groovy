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
class ShellTask extends MetricsTask{
    protected static final String COMMAND_KEY = "shellCommand"
    protected static final String WORKING_DIR_KEY = "shellWorkingDir"
    protected static final String OUTPUT_FILE_KEY = "shellOutputFile"
    protected static final String ERROR_FILE_KEY = "shellErrorFile"

    def runCommand(String command, String workingDirectory, outputFile, errorFile){
        Process process = command.execute(new String[0] , new File(workingDirectory))
        FileOutputStream out = new FileOutputStream(outputFile)
        FileOutputStream err = new FileOutputStream(errorFile)
        process.waitForProcessOutput(out, err)

        if(process.exitValue() > 0 ){
            throw new RuntimeException("Command failed with exit value ${process.exitValue()} " + new File(errorFile).text)
        }

        out.close()
        err.close()
    }

    @Override
    void executeWithMetrics(DelegateExecution execution) {
        def command = execution.getVariable(COMMAND_KEY)
        def workingDir = execution.getVariable(WORKING_DIR_KEY)
        ProcessResult result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()

        def outputFile =execution.getVariable(OUTPUT_FILE_KEY)?: File.createTempFile("output", ".txt")
        def errorFile = execution.getVariable(ERROR_FILE_KEY)?:File.createTempFile("error", ".txt")

        try{
            runCommand(command, workingDir, outputFile, errorFile)
        } catch(e){
            result.error.message = e.getMessage()
            result.message = e.getMessage()
        }

        result.data.shellOutputFile = outputFile
        result.data.shellErrorFile = errorFile
        execution.setVariable(ProcessResult.RESULT_KEY, result)
    }
}
