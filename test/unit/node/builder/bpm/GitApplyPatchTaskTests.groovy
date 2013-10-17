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

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.junit.After
import org.junit.Before

@TestMixin(ControllerUnitTestMixin)
class GitApplyPatchTaskTests extends BPMNTaskTestBase{
    def localPath = File.createTempFile("local", Long.toString(System.nanoTime())).getPath()
    def localPath2 = File.createTempFile("local", Long.toString(System.nanoTime())).getPath()
    def remotePath = File.createTempFile("remote", Long.toString(System.nanoTime())).getPath()
    Git git
    Git remoteGit

    @Before
    void setup(){
        def workingCopy = new File(localPath)
        assert workingCopy.delete()
        localPath = workingCopy.absolutePath

        def remoteCopy = new File(remotePath)
        assert remoteCopy.delete()
        remotePath = remoteCopy.absolutePath

        assert new File(localPath2).delete()

        //setup repo
        setupRemoteRepo(remoteCopy)
    }

    def setupLocalRepo(workingCopy){
        Git.cloneRepository()
                .setURI(remotePath)
                .setDirectory(workingCopy)
                .call();

        def localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo)

        assert workingCopy.exists()
        assert (new File(localPath + "/.git")).exists()
    }

    def setupRemoteRepo(remoteCopy){
        assert Git.init()
                .setDirectory(remoteCopy)
                .call()

        new File("${remoteCopy.absolutePath}/thisisatest.txt").write("test ${Long.toString(System.nanoTime())}\n")
        def remoteRepo = new FileRepository(remoteCopy.absolutePath + "/.git");
        remoteGit = new Git(remoteRepo)
        remoteGit.add()
            .addFilepattern("thisisatest.txt")
            .call()

        remoteGit.commit()
            .setMessage("initial test repo")
            .call()
    }

    def changeRepo(newFile = false){
        new File("${remotePath}/thisisatest.txt").append("test ${Long.toString(System.nanoTime())}\n")

        if(newFile){
            new File("${remotePath}/thisisatest2.txt").append("test ${Long.toString(System.nanoTime())}\n")
        }

        remoteGit.add()
                .addFilepattern("thisisatest2.txt")
                .call()

        def result = remoteGit.commit()
                .setMessage("testing change")
                .setAll(true)
                .call()
        assert result.id != null
    }


    def testApplyPatch(){
        def task = new GitMonitorTask()
        setupLocalRepo(new File(localPath))
        setupLocalRepo(new File(localPath2))
        changeRepo()
        changeRepo(true)
        changeRepo()
        def variables = [localPath: localPath, remotePath: remotePath, branch: "master", remoteBranch: "origin/master"]
        task.execute(mockDelegateExecutionWithVariables(variables, 4, 1))

        assert variables.result.data.repositoryDidChange


        variables = [localPath: localPath2, gitPatch: variables.result.data.repositoryPatchFile]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 4, 1)
        task = new GitApplyPatchTask()
        task.execute(delegateExecution)

        assert variables.result.message.contains("Successfully applied patch")
    }


    @After
    void tearDown(){
        new File(localPath).deleteDir()
        new File(localPath2).deleteDir()
        new File(remotePath).deleteDir()
    }
}
