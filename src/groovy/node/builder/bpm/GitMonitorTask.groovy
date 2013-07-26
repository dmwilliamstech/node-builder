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

        def localCopy = new File(localPath)
        if(!localCopy.exists()){
            Git.cloneRepository()
                    .setURI(remotePath)
                    .setDirectory(localCopy)
                    .call();
            assert new File(localPath).exists()
            delegateExecution.setVariable("repositoryDidChange", false)
            return
        }
        def localRepo = new FileRepository(localPath + "/.git");
        def git = new Git(localRepo)

        def pullResult = git.pull()
                            .call()

        def results = [:]
        results.successful = pullResult.successful

        def fetchResult = pullResult.fetchResult
//        results.advertisedRefs = fetchResult.advertisedRefs
//        results.trackingRefUpdates = fetchResult.trackingRefUpdates
        results.messages = fetchResult.messages
        results.uri = fetchResult.getURI()

        def mergeResult = pullResult.mergeResult
        if(mergeResult){
            results.mergeBase = mergeResult.base
            results.mergeCheckoutConflicts = mergeResult.checkoutConflicts
            results.mergeConflicts = mergeResult.conflicts
            results.mergeFailingPaths = mergeResult.failingPaths
            results.mergedCommits = mergeResult.mergedCommits
            results.mergeStatus = mergeResult.mergeStatus
            results.mergeNewHead = mergeResult.newHead
        }

        def rebaseResult = pullResult.rebaseResult
        if(rebaseResult){
            results.rebaseConflicts = rebaseResult.conflicts
            results.rebaseCurrentCommit = rebaseResult.currentCommit
            results.rebaseFailingPaths = rebaseResult.failingPaths
            results.rebaseStatus = rebaseResult.status
        }
        delegateExecution.setVariable("repositoryDidChange", !fetchResult.trackingRefUpdates.empty)
        delegateExecution.setVariable("repositoryChangeResults", results)
    }

    def serialize
}
