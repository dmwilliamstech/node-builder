package node.builder.bpm

import node.builder.exceptions.UnknownGitRepositoryException
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser

class GitMonitorTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {
        //check dir exists

        def remotePath = delegateExecution.getVariable("remotePath")
        def localPath = delegateExecution.getVariable("localPath")
        def result = new ProcessResult()
        result.data = [:]

        def localCopy = new File(localPath)
        if(!localCopy.exists()){
            delegateExecution.setVariable("result", doInitialClone(remotePath, localCopy, localPath, result))
            return
        }

        def localRepo = new FileRepository(localPath + "/.git");
        def git = new Git(localRepo)

        getDiffFromRemoteMaster(git, result)

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

        result.data.repositoryDidChange = false
        return result
    }


    def setResultWithPullResult(PullResult pullResult, ProcessResult result){
        def fetchResult = pullResult.fetchResult
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
    }

    String getDiffFromRemoteMaster(Git git, result){
        git.fetch()
                .call()

        ObjectId headId = git.getRepository().resolve("origin/master")
        ObjectId oldId = git.getRepository().resolve("master")
        def file = File.createTempFile("patch", "patch")

        OutputStream out = new FileOutputStream(file)
        git.diff()
                .setNewTree(getTreeIterator(headId, git))
                .setOldTree(getTreeIterator(oldId, git))
                .setOutputStream(out)
                .call();

        out.flush()
        out.close()
        result.data.repositoryPatchFile = file.path
        result.data.repositoryDidChange = file.size() > 0
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
