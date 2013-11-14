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

import org.junit.After
import org.junit.Before
import org.junit.Test

class DownloadFileTaskTests extends BPMNTaskTestBase{
    def files = []
    @Before
    void setup(){

    }

	@Test
    void shouldDownloadFile(){
        def task = [streamFile: {outputFile, remoteUrl, delegateExecution -> "You've been mocked"}] as DownloadFileTask

        def variables = [downloadFileUrl:"https://server.com/path/file.zip", downloadFileOutputPath:'/tmp/master.zip']
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)

        task.execute(delegateExecution)

        assert variables.result.data.downloadedFile == '/tmp/master.zip'
        files << variables.result.data.downloadedFile
    }


	@Test(expected = NullPointerException)
    void shouldDetectMissingDownloadUrl(){
        def task = new DownloadFileTask()
        def variables = [downloadFileOutputPath:'/tmp/master.zip']
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 1)
        task.execute(delegateExecution)
    }

    @After
    void tearDown(){
        files.each{ file ->
            new File(file).delete()
        }
    }
}
