package node.builder.bpm

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import groovy.time.TimeCategory
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
class FolderMonitorTaskTests extends BPMNTaskTestBase {
    def files = []

    @Before
    void setup(){
        files << new File('/tmp/test_folder_monitor.txt')
        files.last().write("testing!!!")
        files << new File('/tmp/test_folder')
        files.last().mkdirs()
        files << new File('/tmp/test_folder/test_folder_monitor.txt')
        files.last().write("testing!!!")
    }


    @Test
    void shouldDetectFilesFromADate(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as FolderMonitorTask

        def variables = [:]
        variables.put(FolderMonitorTask.FOLDER_MONITOR_PATH_KEY, "/tmp")
        variables.put(FolderMonitorTask.FOLDER_MONITOR_LAST_UPDATED_KEY, new Date())

        task.execute(mockDelegateExecutionWithVariables(variables,3, 1))

        assert variables.result.wasSuccessful()
        def message = "Successfully ran file monitor on target /tmp"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')

    }

    @Test
    void shouldDetectFilesFromADateString(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as FolderMonitorTask

        def variables = [:]
        variables.put(FolderMonitorTask.FOLDER_MONITOR_PATH_KEY, "/tmp")
        variables.put(FolderMonitorTask.FOLDER_MONITOR_LAST_UPDATED_KEY, new Date().toString())

        task.execute(mockDelegateExecutionWithVariables(variables,3, 1))

        assert variables.result.wasSuccessful()
        def message = "Successfully ran file monitor on target /tmp"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')

    }

    @Test
    void shouldDetectFilesNewerThanThePreviousRun(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as FolderMonitorTask

        def variables = [:]
        variables.put(FolderMonitorTask.FOLDER_MONITOR_PATH_KEY, "/tmp")
        use(TimeCategory) {
            variables.put(FolderMonitorTask.FOLDER_MONITOR_LAST_UPDATED_KEY, new Date() - 5.seconds)
        }
        task.execute(mockDelegateExecutionWithVariables(variables,3, 1))

        assert variables.result.wasSuccessful()
        def message = "Successfully ran file monitor on target /tmp"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')

        assert variables.result.data.fileSystemDidChange
        assert variables.result.data.folderMonitorFileDetectionList.size() == 2
        assert variables.result.data.folderMonitorFileDetectionList.find{ it -> return it == files.first().path }
        assert variables.result.data.folderMonitorFileDetectionList.find{ it -> return it == files.last().path }
    }

    @Test
    void shouldNotDetectFilesOlderThanThePreviousRun(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as FolderMonitorTask

        def variables = [:]
        variables.put(FolderMonitorTask.FOLDER_MONITOR_PATH_KEY, "/tmp")
        use(TimeCategory) {
            variables.put(FolderMonitorTask.FOLDER_MONITOR_LAST_UPDATED_KEY, new Date() + 5.seconds)
        }
        task.execute(mockDelegateExecutionWithVariables(variables,3, 1))

        assert variables.result.wasSuccessful()
        def message = "Successfully ran file monitor on target /tmp"
        assert variables.result.message.replaceAll('\n','') == message.replaceAll('\n','')

        assert !variables.result.data.fileSystemDidChange
        assert variables.result.data.folderMonitorFileDetectionList.empty
    }

    @Test
    void shouldFailIfFolderIsNotFound(){
        def task =  [uploadFile: {file, hostname, username, keyPath, destPath -> }] as FolderMonitorTask

        def variables = [:]
        variables.put(FolderMonitorTask.FOLDER_MONITOR_PATH_KEY, "/notreal")
        variables.put(FolderMonitorTask.FOLDER_MONITOR_LAST_UPDATED_KEY, new Date())

        task.execute(mockDelegateExecutionWithVariables(variables,3, 1))

        assert !variables.result.wasSuccessful()
        def message = "Target /notreal must exist and be a directory"
        assert variables.result.error.message.replaceAll('\n','') == message.replaceAll('\n','')
    }

    @After
    void tearDown(){
        files.each { File file ->
            file.delete() ?: file.deleteDir()
        }
    }

}
