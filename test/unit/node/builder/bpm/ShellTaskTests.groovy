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
class ShellTaskTests  extends BPMNTaskTestBase {
    def files = []

    @Before
    void setup(){

    }

    @Test
    void shouldRunAShellCommand(){
        def task = new ShellTask()
        def output = File.createTempFile('output', '.txt')
        def error = File.createTempFile('error', '.txt')
        files << output
        files << error

        def variables = ["shellCommand": "echo hello",
                "shellWorkingDir": '/tmp',
                "shellOutputFile": output.path,
                "shellErrorFile": error.path]
        task.execute(mockDelegateExecutionWithVariables(variables,5, 2))

        assert variables.result.wasSuccessful()
    }

    @Test
    void shouldHandleAnError(){
        def task = new ShellTask()
        def output = File.createTempFile('output', '.txt')
        def error = File.createTempFile('error', '.txt')
        files << output
        files << error

        def variables = ["shellCommand": "fail",
                "shellWorkingDir": '/tmp',
                "shellOutputFile": output.path,
                "shellErrorFile": error.path]
        task.execute(mockDelegateExecutionWithVariables(variables,5, 2))

        assert !variables.result.wasSuccessful()
    }

    @After
    void tearDown(){
        files.each { File file ->
            file.delete()
        }
    }

}
