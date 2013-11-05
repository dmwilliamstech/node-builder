package node.builder.bpm

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.After
import org.junit.Before
import org.junit.Ignore
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
class NexusStorageTaskTests extends BPMNTaskTestBase {
    def files = []

    @Before
    void setup(){

    }


    @Test
    void shouldUploadFilesFromAList(){
        def task =  [runCommand: { command, workingDir, outputFile, errorFile -> }] as NexusStorageTask

        def variables = [:]
        variables.put(NexusStorageTask.NEXUS_REPO_FILE_LIST_KEY, ['./activiti.lock.db','./activiti.h2.db','third.file'])
        variables.put(NexusStorageTask.NEXUS_REPO_USER_KEY, "name")
        variables.put(NexusStorageTask.NEXUS_REPO_PASSWORD_KEY, "password")
        variables.put(NexusStorageTask.NEXUS_REPO_URL_KEY, "http://rizzo/nexus/content/repositories/releases/")
        variables.put("projectName", "nexus-storage-test")
        variables.put(NexusStorageTask.NEXUS_REPO_VERSION_KEY, UUID.randomUUID().toString())

        task.execute(mockDelegateExecutionWithVariables(variables,11, 6))

        assert variables.result.wasSuccessful()
        def message = "Uploaded activiti.lock.db to http://rizzo/nexus/content/repositories/releases//projects/nexus-storage-test/${variables.get(NexusStorageTask.NEXUS_REPO_VERSION_KEY)}/activiti.lock.db\n" +
                "Uploaded activiti.h2.db to http://rizzo/nexus/content/repositories/releases//projects/nexus-storage-test/${variables.get(NexusStorageTask.NEXUS_REPO_VERSION_KEY)}/activiti.h2.db\n" +
                "Uploaded third.file to http://rizzo/nexus/content/repositories/releases//projects/nexus-storage-test/${variables.get(NexusStorageTask.NEXUS_REPO_VERSION_KEY)}/third.file"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')
        assert variables.result.data.artifactUrls.size() == 3
    }

    @Test
    void shouldUploadFilesFromADelimitedString(){
        def task =  [runCommand: {command, workingDir, outputFile, errorFile -> }] as NexusStorageTask

        def variables = [:]
        variables.put(NexusStorageTask.NEXUS_REPO_FILE_LIST_KEY, './activiti.lock.db,./activiti.h2.db,third.file')
        variables.put(NexusStorageTask.NEXUS_REPO_USER_KEY, "name")
        variables.put(NexusStorageTask.NEXUS_REPO_PASSWORD_KEY, "password")
        variables.put(NexusStorageTask.NEXUS_REPO_URL_KEY, "http://rizzo/nexus/content/repositories/releases/")
        variables.put("projectName", "nexus-storage-test")
        variables.put(NexusStorageTask.NEXUS_REPO_VERSION_KEY, UUID.randomUUID().toString())

        task.execute(mockDelegateExecutionWithVariables(variables,11, 6))

        assert variables.result.wasSuccessful()
        def message = "Uploaded activiti.lock.db to http://rizzo/nexus/content/repositories/releases//projects/nexus-storage-test/${variables.get(NexusStorageTask.NEXUS_REPO_VERSION_KEY)}/activiti.lock.db\n" +
                "Uploaded activiti.h2.db to http://rizzo/nexus/content/repositories/releases//projects/nexus-storage-test/${variables.get(NexusStorageTask.NEXUS_REPO_VERSION_KEY)}/activiti.h2.db\n" +
                "Uploaded third.file to http://rizzo/nexus/content/repositories/releases//projects/nexus-storage-test/${variables.get(NexusStorageTask.NEXUS_REPO_VERSION_KEY)}/third.file"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')
        assert variables.result.data.artifactUrls.size() == 3
    }

    @Test
    void shouldFailIfNexusIsNotFound(){
        def task =  [runCommand: { command, workingDir, outputFile, errorFile -> throw new Exception("Could not resolve host") }] as NexusStorageTask

        def variables = [:]
        variables.put(NexusStorageTask.NEXUS_REPO_FILE_LIST_KEY, './activiti.lock.db,./activiti.h2.db')
        variables.put(NexusStorageTask.NEXUS_REPO_USER_KEY, "name")
        variables.put(NexusStorageTask.NEXUS_REPO_PASSWORD_KEY, "password")
        variables.put(NexusStorageTask.NEXUS_REPO_URL_KEY, "http://notreal/nexus/content/repositories/releases/")
        variables.put("projectName", "nexus-storage-test")
        variables.put(NexusStorageTask.NEXUS_REPO_VERSION_KEY, UUID.randomUUID().toString())

        task.execute(mockDelegateExecutionWithVariables(variables,10, 6))

        assert !variables.result.wasSuccessful()
        def message = "Could not resolve host"
        assert variables.result.error.message.contains(message)
    }

    @After
    void tearDown(){
        files.each { File file ->
            file.delete()
        }
    }

}
