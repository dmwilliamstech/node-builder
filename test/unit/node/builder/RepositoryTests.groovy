package node.builder

import grails.test.mixin.TestFor

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Repository)
class RepositoryTests {

    void testCreate() {
        def repo = new Repository(name: "Some Repo", localPath: "/path", remotePath:"https://repo", workflowKey: "someWorkflow")
        repo.save()
        assert !repo.hasErrors()

        def first = Repository.all.first()
        assert first.name == "Some Repo"
        assert first.localPath == "/path"
        assert first.remotePath == "https://repo"
        assert first.workflowKey == "someWorkflow"

    }
}