package node.builder.bpm

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.After
import org.junit.Before
import org.junit.Test
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

@TestMixin(ControllerUnitTestMixin)
class FileParserTaskTests extends BPMNTaskTestBase {
    def files = []

    @Before
    void setup(){

    }

    @Test
    void shouldParseAStringFromAFile(){
        def task = new FileParserTask()

        def variables = [:]
        variables.put(FileParserTask.OUTPUT_KEY, "putoutputhere")
        variables.put(FileParserTask.FILENAME, new ClassPathResource("resources/console.txt").file.path)
        variables.put(FileParserTask.PARSE_STRING, /POST\s(.*)/)
        task.execute(mockDelegateExecutionWithVariables(variables,5, 1))

        assert variables.result.wasSuccessful()
        assert variables.result.data['putoutputhere'].first() == 'http://rizzo/nexus/content/repositories/gems/gems/mongo-2.0.0.omega.gem'
    }

    @Test
    void shouldParseAllStringsFromAFile(){
        def task = new FileParserTask()

        def variables = [:]
        variables.put(FileParserTask.OUTPUT_KEY, "putoutputhere")
        variables.put(FileParserTask.FILENAME, new ClassPathResource("resources/console.txt").file.path)
        variables.put(FileParserTask.PARSE_STRING, /bundler/)
        task.execute(mockDelegateExecutionWithVariables(variables,5, 1))

        assert variables.result.wasSuccessful()
        assert variables.result.data['putoutputhere'].size() == 8
    }

    @After
    void tearDown(){
        files.each { File file ->
            file.delete()
        }
    }

}
