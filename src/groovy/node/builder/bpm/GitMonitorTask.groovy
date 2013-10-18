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

import node.builder.exceptions.UnknownGitRepositoryException
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.errors.TransportException

import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser

import javax.management.RuntimeErrorException


class GitMonitorTask extends MetricsTask{

    void executeWithMetrics(DelegateExecution delegateExecution) throws Exception {
        def remotePath = delegateExecution.getVariable("remotePath")
        def localPath = delegateExecution.getVariable("localPath")
        def branch = delegateExecution.getVariable("branch")
        def remoteBranch = delegateExecution.getVariable("remoteBranch")
        def result = new ProcessResult()
        result.data = [:]

        def localCopy = new File(localPath)
        if(!localCopy.exists()){
            delegateExecution.setVariable("result", doInitialClone(remotePath, localCopy, localPath, result))
            return
        }

        def localRepo = new FileRepository(localPath + "/.git");
        def git = new Git(localRepo)

        getDiffFromRemoteMaster(git, result, branch, remoteBranch)

        def pullResult = git.pull()
                            .call()

        result.data.reference = localRepo.resolve("HEAD").name

        setResultWithPullResult(pullResult, result)

        delegateExecution.setVariable("result", result)
    }

    def doInitialClone(String remotePath, File localCopy, String localPath, ProcessResult result){
        try{
            Git.cloneRepository()
                    .setURI(remotePath)
                    .setDirectory(localCopy)
                    .call();
        }catch (TransportException e){
            throw new UnknownGitRepositoryException("Could not connect to remote repository $remotePath " + e.getMessage(), e)
        }

        result.message = "Initial clone created, no difference detected"
        result.data.repositoryDidChange = false
        return result
    }


    def setResultWithPullResult(PullResult pullResult, ProcessResult result){
        def fetchResult = pullResult.fetchResult
        result.data.uri = fetchResult.getURI().toString()

        def mergeResult = pullResult.mergeResult
        if(mergeResult){
            result.data.mergeBase = mergeResult.base
            result.data.mergeCheckoutConflicts = mergeResult.checkoutConflicts
            result.data.mergeConflicts = mergeResult.conflicts
            result.data.mergeFailingPaths = mergeResult.failingPaths
            result.data.mergedCommits = mergeResult.mergedCommits
            result.data.mergeStatus = mergeResult.mergeStatus
            result.data.mergeNewHead = mergeResult.newHead
        }

        def rebaseResult = pullResult.rebaseResult
        if(rebaseResult){
            result.data.rebaseConflicts = rebaseResult.conflicts
            result.data.rebaseCurrentCommit = rebaseResult.currentCommit
            result.data.rebaseFailingPaths = rebaseResult.failingPaths
            result.data.rebaseStatus = rebaseResult.status
        }

        result.message = result.data.repositoryDidChange?
            "Difference detected from remote repository, generated patch and merged local" :
            "No difference detected from remote repository at this time"
    }


    def runCommand(command, git, outputFile, errorFile){
        Process process = command.execute(new String[0] , git.repository.workTree)
        FileOutputStream out = new FileOutputStream(outputFile)
        FileOutputStream err = new FileOutputStream(errorFile)
        process.waitForProcessOutput(out, err)

        if(process.exitValue() > 0 ){
            throw new RuntimeException("Failed to run git command ${command} " + new File(errorFile).text)
        }

        out.close()
        err.close()
    }

    def createTempFile(name, extension){
        return File.createTempFile(name, extension).path
    }


    String getDiffFromRemoteMaster(Git git, result, branch, remoteBranch){
        def outs = []

        def reference = runCommand("git rev-parse HEAD", git, createTempFile("output", ".txt"), createTempFile("error", ".txt"))

        [
            [string:"git fetch", outputFile: createTempFile("output", ".txt"), errorFile: createTempFile("error", ".txt")],
            [string:"git checkout $remoteBranch", outputFile: createTempFile("output", ".txt"), errorFile: createTempFile("error", ".txt")],
            [string:"git format-patch $branch --stdout", outputFile: createTempFile("patch", ".patch"), errorFile: createTempFile("error", ".txt")],
            [string:"git checkout $branch", outputFile: createTempFile("output", ".txt"), errorFile: createTempFile("error", ".txt")]
        ].each{ command ->
            runCommand(command.string, git, command.outputFile, command.errorFile)
            outs.add(command.outputFile)
        }
        result.data.repositoryPatchFile = outs[2]
        result.data.repositoryDidChange = new File(outs[2]).size() > 0

    }

    private AbstractTreeIterator getTreeIterator(ObjectId id, Git git)
    throws IOException {
        final CanonicalTreeParser p = new CanonicalTreeParser();
        final ObjectReader or = git.repository.newObjectReader();
        try {
            p.reset(or, new RevWalk(git.repository).parseTree(id));
            return p;
        } finally {
            or.release();
        }
    }

}
