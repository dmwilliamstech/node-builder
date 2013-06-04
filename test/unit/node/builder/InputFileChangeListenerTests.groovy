package node.builder



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

@Mock([Node,NodeConfiguration,Application,ApplicationConfiguration])
class InputFileChangeListenerTests {

    void testNewFileWithEmptyApplicationData() {
        Resource resource = new ClassPathResource("resources/empty_application.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Application.count == 0
    }

    void testNewFileWithSingleApplicationData() {
        Resource resource = new ClassPathResource("resources/single_application.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Application.count == 1
        assert Application.first().name == "Some Application"

        assert ApplicationConfiguration.count == 1
    }

    void testNewFileWithSingleNodeData() {
        Resource resource = new ClassPathResource("resources/single_node.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Node.count == 1
        assert Node.first().name == "Some Node"

        assert Application.count == 2
        assert Application.first().name == "Some Super Application"
        assert Application.last().name == "Some Other Application"

        assert ApplicationConfiguration.count == 2
        assert NodeConfiguration.count == 1

        assert Node.first().configurations.first().name == "Some Node Config"
        assert Application.first().configurations.first().name == "Some App Config"
    }
}
