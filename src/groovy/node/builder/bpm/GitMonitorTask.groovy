package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.IndexDiff
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.FileTreeIterator
import org.eclipse.jgit.treewalk.WorkingTreeIterator
import org.eclipse.jgit.util.io.DisabledOutputStream

import javax.management.RuntimeErrorException
import java.text.MessageFormat



class GitMonitorTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {
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

        result.data.diff = getDiffFromRemoteMaster(git)

        def pullResult = git.pull()
                            .call()

        def fetchResult = pullResult.fetchResult
//        result.data.advertisedRefs = fetchResult.advertisedRefs
//        result.data.trackingRefUpdates = fetchResult.trackingRefUpdates
        result.data.reference = localRepo.resolve("HEAD").name
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

        result.data.repositoryDidChange = !result.data.diff.empty

        delegateExecution.setVariable("result", result)
    }

    def runCommand(command, git){
        def process = command.execute(new String[0] , git.repository.workTree)
        OutputStream out = new ByteArrayOutputStream(), err = new ByteArrayOutputStream()
        process.consumeProcessOutput(out, err)
        process.waitFor()
        if(process.exitValue() > 0 ){
            throw new RuntimeErrorException("Failed to run git commands " + err)
        }

        return out.toString()
    }

    String getDiffFromRemoteMaster(Git git){
        def outs = []

        def reference = runCommand("git rev-list --max-parents=0 HEAD", git)

        ["git fetch",
        "git checkout origin/master",
        "git format-patch master --stdout",
        "git checkout master"].each{ command ->
            def out = runCommand(command, git)
            outs.add(out)
        }
        new File("/tmp/patch.patch").write(outs[2])
        return outs[2]
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
