package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.springframework.core.io.ClassPathResource

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
class FileParserTask extends MetricsTask{
    protected static final String PARSE_STRING = "filerParserParseString"
    protected static final String OUTPUT_KEY = "fileParserOutputKey"
    protected static final String FILENAME = "fileParserFilename"




    @Override
    void executeWithMetrics(DelegateExecution execution) {
        def regex = execution.getVariable(PARSE_STRING)
        def filename = execution.getVariable(FILENAME)
        def outputKey = execution.getVariable(OUTPUT_KEY)
        ProcessResult result = execution.getVariable(ProcessResult.RESULT_KEY)?: new ProcessResult()
        def matches = execution.getVariable(outputKey)?: []

        def textFile =  new File(filename)

        textFile.eachLine { line ->
            line.findAll(regex){ ms ->
                1..ms.size()-1.each {
                    matches.addAll(ms[it])
                }
            }
        }

        result.data[outputKey] = matches
        execution.setVariable(ProcessResult.RESULT_KEY, result)
    }
}
