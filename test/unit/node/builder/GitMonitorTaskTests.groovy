package node.builder

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import groovy.mock.interceptor.MockFor
import node.builder.bpm.GitMonitorTask
import org.activiti.engine.delegate.DelegateExecution
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.junit.Before

@TestMixin(ControllerUnitTestMixin)
class GitMonitorTaskTests {
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

    def mockDelegateExecutionWithVariables(variables, variablecount){
        def delegateExecution = new MockFor(DelegateExecution.class)

        delegateExecution.demand.getVariable(){variable -> variables[variable]}
        delegateExecution.demand.getVariable(){variable -> variables[variable]}

        (0..variablecount).each {
            delegateExecution.demand.setVariable(){name, value -> variables[name] = value}
        }
        return delegateExecution.proxyInstance()
    }

    def testCloneInitial(){
        def task = new GitMonitorTask()
        def variables = [localPath: localPath, remotePath: remotePath]
        task.execute(mockDelegateExecutionWithVariables(variables,1))

        assert (new File(localPath)).exists()
        assert !variables.repositoryDidChange
    }

    def testDetectChange(){
        def task = new GitMonitorTask()
        setupLocalRepo(new File(localPath))
        changeRepo()
        def variables = [localPath: localPath, remotePath: remotePath]
        task.execute(mockDelegateExecutionWithVariables(variables,2))

        assert variables.repositoryDidChange
    }

    def testDetectNoChange(){
        def task = new GitMonitorTask()
        setupLocalRepo(new File(localPath))
        def variables = [localPath: localPath, remotePath: remotePath]
        task.execute(mockDelegateExecutionWithVariables(variables,2))
        assert !variables.repositoryDidChange
    }

}
