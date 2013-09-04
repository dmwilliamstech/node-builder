package node.builder

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import groovy.mock.interceptor.MockFor
import node.builder.bpm.GitApplyPatchTask
import node.builder.bpm.GitMonitorTask
import org.activiti.engine.delegate.DelegateExecution
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.junit.Before

@TestMixin(ControllerUnitTestMixin)
class GitApplyPatchTaskTests {
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

    def mockDelegateExecutionWithVariables(variables, variablecount){
        def delegateExecution = new MockFor(DelegateExecution.class)

        delegateExecution.demand.getVariable(){variable -> variables[variable]}
        delegateExecution.demand.getVariable(){variable -> variables[variable]}

        (0..variablecount).each {
            delegateExecution.demand.setVariable(){name, value -> variables[name] = value}
        }
        return delegateExecution.proxyInstance()
    }

    def testApplyPatch(){
        def task = new GitMonitorTask()
        setupLocalRepo(new File(localPath))
        setupLocalRepo(new File(localPath2))
        changeRepo()
        changeRepo(true)
        changeRepo()
        def variables = [localPath: localPath, remotePath: remotePath]
        task.execute(mockDelegateExecutionWithVariables(variables,2))

        assert variables.result.data.repositoryDidChange


        variables = [localPath: localPath2, gitPatch: variables.result.data.repositoryPatchFile]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 2)
        task = new GitApplyPatchTask()
        task.execute(delegateExecution)

        assert variables.result.message.contains("Successfully applied patch")
    }
}
