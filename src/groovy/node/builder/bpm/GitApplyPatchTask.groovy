package node.builder.bpm

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser

import javax.management.RuntimeErrorException

class GitApplyPatchTask implements JavaDelegate{

    void execute(DelegateExecution delegateExecution) throws Exception {
        def localPath = delegateExecution.getVariable("localPath")
        def patch = delegateExecution.getVariable("gitPatch")

        ProcessResult result = new ProcessResult()
        result.data = [:]

        def localCopy = new File(localPath)
        if(!localCopy.exists()){
            initializeRepository(localCopy)
            assert new File(localPath).exists()
        }

        def out = applyPatch(patch, localCopy)

        result.message = "Successfully applied patch " + out
        delegateExecution.setVariable("result", result)
    }

    def runCommand(command, workingDirectory){
        def process = command.execute(new String[0] , workingDirectory)
        OutputStream out = new ByteArrayOutputStream(), err = new ByteArrayOutputStream()
        process.consumeProcessOutput(out, err)
        process.waitFor()
        if(process.exitValue() > 0 ){
            throw new RuntimeException("Failed to run command ${command}" + err)
        }
        return out.toString()
    }

    def applyPatch(patch, localCopy){
        def patchFile = File.createTempFile("patch",".p")
        patchFile.write(patch)
        return runCommand("git am --signoff ${patchFile.path}", localCopy)
    }

    def initializeRepository(localCopy){
        localCopy.mkdirs()
        return runCommand("git init", localCopy)
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
