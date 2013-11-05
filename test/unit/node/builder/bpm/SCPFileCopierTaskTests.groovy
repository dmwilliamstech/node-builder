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
class SCPFileCopierTaskTests extends BPMNTaskTestBase {
    def files = []

    @Before
    void setup(){

    }


    @Test
    void shouldUploadFilesFromAList(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as SCPFileCopierTask

        def variables = [:]
        variables.put(SCPFileCopierTask.SCP_SRC_FILE_LIST_KEY, ['./activiti.lock.db','./activiti.h2.db','third.file'])
        variables.put(SCPFileCopierTask.SCP_USER_KEY, "name")
        variables.put(SCPFileCopierTask.SCP_KEY_PATH_KEY, "/path/to/key")
        variables.put(SCPFileCopierTask.SCP_HOSTNAME_KEY, "localhost")
        variables.put(SCPFileCopierTask.SCP_DEST_FILE_PATH_KEY, "/dest/path")

        task.execute(mockDelegateExecutionWithVariables(variables,11, 6))

        assert variables.result.wasSuccessful()
        def message = "File ./activiti.lock.db copied to name@localhost:/dest/path\n" +
                "File ./activiti.h2.db copied to name@localhost:/dest/path\n" +
                "File third.file copied to name@localhost:/dest/path"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')

    }

    @Test
    void shouldUploadFilesFromADelimitedString(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as SCPFileCopierTask

        def variables = [:]
        variables.put(SCPFileCopierTask.SCP_SRC_FILE_LIST_KEY, './activiti.lock.db,./activiti.h2.db,third.file')
        variables.put(SCPFileCopierTask.SCP_USER_KEY, "name")
        variables.put(SCPFileCopierTask.SCP_KEY_PATH_KEY, "/path/to/key")
        variables.put(SCPFileCopierTask.SCP_HOSTNAME_KEY, "localhost")
        variables.put(SCPFileCopierTask.SCP_DEST_FILE_PATH_KEY, "/dest/path")

        task.execute(mockDelegateExecutionWithVariables(variables,11, 6))

        assert variables.result.wasSuccessful()
        def message = "File ./activiti.lock.db copied to name@localhost:/dest/path\n" +
                "File ./activiti.h2.db copied to name@localhost:/dest/path\n" +
                "File third.file copied to name@localhost:/dest/path"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')

    }

    @Test
    void shouldFailIfHostnameIsNotFound(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> throw new UnknownHostException("somehost is not found") }] as SCPFileCopierTask

        def variables = [:]
        variables.put(SCPFileCopierTask.SCP_SRC_FILE_LIST_KEY, ['./activiti.lock.db,./activiti.h2.db,third.file'])
        variables.put(SCPFileCopierTask.SCP_USER_KEY, "name")
        variables.put(SCPFileCopierTask.SCP_KEY_PATH_KEY, "/path/to/key")
        variables.put(SCPFileCopierTask.SCP_HOSTNAME_KEY, "somehost")
        variables.put(SCPFileCopierTask.SCP_DEST_FILE_PATH_KEY, "/dest/path")

        task.execute(mockDelegateExecutionWithVariables(variables,11, 6))

        assert !variables.result.wasSuccessful()
        def message = "somehost is not found"
        assert variables.result.error.message.contains(message)
    }

    @After
    void tearDown(){
        files.each { File file ->
            file.delete()
        }
    }

}
