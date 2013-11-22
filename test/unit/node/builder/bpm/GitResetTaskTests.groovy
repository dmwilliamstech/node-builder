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
import node.builder.exceptions.UnknownGitRepositoryException
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.junit.After
import org.junit.Before
import org.junit.Test

@TestMixin(ControllerUnitTestMixin)
class GitResetTaskTests extends BPMNTaskTestBase {
    def localPath = File.createTempFile("local", Long.toString(System.nanoTime())).getPath()
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

    def changeRepo(){
        new File("${remotePath}/thisisatest.txt").append("test ${Long.toString(System.nanoTime())}\n")

        def result = remoteGit.commit()
                .setMessage("testing change")
                .setOnly("thisisatest.txt")
                .call()

        assert result.id != null
    }

    @Test
    void shouldResetARepoToTheOriginalHead(){
        def workingCopy = new File(localPath)
        setupLocalRepo(workingCopy)

        def task = new GitResetOrigHeadTask()
        def variables = [gitResetOriginHeadLocalPath: localPath]

        def originalId = "git rev-parse --verify HEAD".execute([], workingCopy).text

        changeRepo()

        "git pull".execute([], workingCopy).waitFor()

        assert originalId != "git rev-parse --verify HEAD".execute([], workingCopy).text

        task.execute(mockDelegateExecutionWithVariables(variables,4, 2))

        assert originalId == "git rev-parse --verify HEAD".execute([], workingCopy).text
        assert variables.result.message == "Working copy reset to ORIG_HEAD (${originalId.replaceAll('\n','')})".toString()
    }

    @After
    void tearDown(){
        new File(localPath).deleteDir()
        new File(remotePath).deleteDir()
    }
}
