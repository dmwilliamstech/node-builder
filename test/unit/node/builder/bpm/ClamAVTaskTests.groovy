package node.builder.bpm

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.After
import org.junit.Before
import org.junit.Test

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

@TestMixin(ControllerUnitTestMixin)
class ClamAVTaskTests extends BPMNTaskTestBase {
    def files = []
    def text = """/workspace/node-builder/.gitignore: OK
/workspace/node-builder/activiti.h2.db: OK
/workspace/node-builder/activiti.lock.db: OK
/workspace/node-builder/application.properties: OK
/workspace/node-builder/LICENSE.txt: OK
/workspace/node-builder/node-builder-grailsPlugins.iml: OK
/workspace/node-builder/node-builder.iml: OK
/workspace/node-builder/phantomjs-1.9.2-linux-x86_64.tar.bz2: OK
/workspace/node-builder/README.md: OK

----------- SCAN SUMMARY -----------
Known viruses: 2867818
Engine version: 0.97.8
Scanned directories: 1
Scanned files: 9
Infected files: 0
Data scanned: 65.07 MB
Data read: 14.71 MB (ratio 4.42:1)
Time: 9.851 sec (0 m 9 s)"""

    @Before
    void setup(){

    }

    @Test
    void shouldRunClamscanWithAList(){

        def task = [runCommand: { command, workingDir, outputFile, errorFile -> outputFile.metaClass.text = text }] as ClamAVTask

        def variables = [:]
        variables.put(ClamAVTask.FILE_LIST_KEY, ['somefile','someotherfile'])
        task.execute(mockDelegateExecutionWithVariables(variables,5, 6))

        assert variables.result.wasSuccessful()
        assert variables.result.message == "Scan of file(s) (${['somefile','someotherfile'].join(', ')}) is complete"
        println variables.result.data.clamAVScan
        assert variables.result.data.clamAVScan.summaries[0].name == 'Known viruses'
        assert variables.result.data.clamAVScan.summaries[0].value == '2867818'

    }

    @Test
    void shouldRunClamscanWithDelimitedString(){
        def task = [runCommand: { command, workingDir, outputFile, errorFile -> outputFile.metaClass.text = text }] as ClamAVTask

        def variables = [:]
        variables.put(ClamAVTask.FILE_LIST_KEY, 'somefile,someotherfile')
        task.execute(mockDelegateExecutionWithVariables(variables,5, 6))

        assert variables.result.wasSuccessful()
        assert variables.result.message == "Scan of file(s) (${['somefile','someotherfile'].join(', ')}) is complete"
        assert variables.result.data.clamAVScan.summaries[0].name == 'Known viruses'
        assert variables.result.data.clamAVScan.summaries[0].value == '2867818'
    }

    @Test
    void shouldFailIfClamscanIsNotFound(){
        def task = [runCommand: { command, workingDir, outputFile, errorFile -> throw new Exception("Cannot run program \"clamscan\" (in directory \"/var/folders/09/njqp6l3s5lj8vdbrlzk7n1_40000gn/T\"): error=2, No such file or directory")}] as ClamAVTask

        def variables = [:]
        variables.put(ClamAVTask.FILE_LIST_KEY, 'somefile,someotherfile')
        task.execute(mockDelegateExecutionWithVariables(variables,5, 6))

        assert !variables.result.wasSuccessful()
        assert variables.result.error.message == "Cannot run program \"clamscan\" (in directory \"/var/folders/09/njqp6l3s5lj8vdbrlzk7n1_40000gn/T\"): error=2, No such file or directory"

    }

    @After
    void tearDown(){
        files.each { File file ->
            file.delete()
        }
    }

}
