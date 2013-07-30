package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository



class GitMonitorTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {
        //check dir exists

        def remotePath = delegateExecution.getVariable("remotePath")
        def localPath = delegateExecution.getVariable("localPath")
        def result = new ProcessResult()
        result.data = [:]

        def localCopy = new File(localPath)
        if(!localCopy.exists()){
            Git.cloneRepository()
                    .setURI(remotePath)
                    .setDirectory(localCopy)
                    .call();
            assert new File(localPath).exists()
            result.data.repositoryDidChange = false
            delegateExecution.setVariable("result", result)
            return
        }
        def localRepo = new FileRepository(localPath + "/.git");
        def git = new Git(localRepo)

        def pullResult = git.pull()
                            .call()

        def fetchResult = pullResult.fetchResult
//        result.data.advertisedRefs = fetchResult.advertisedRefs
//        result.data.trackingRefUpdates = fetchResult.trackingRefUpdates
        result.message = fetchResult.messages.toString()
        result.data.uri = fetchResult.getURI()

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

        result.data.repositoryDidChange = !fetchResult.trackingRefUpdates.empty

        delegateExecution.setVariable("result", result)

    }


}
