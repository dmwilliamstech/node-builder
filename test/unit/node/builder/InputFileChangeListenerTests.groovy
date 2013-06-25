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
        def nodeType = new Node(name: "Default", description: "Default Container for nodetype less Applications")
        nodeType.save()

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
        assert Node.first().name == "class::name"
        assert Node.first().flavorId == "2"

        assert Application.count == 2
        assert Application.first().name == "class::app_1"
        assert Application.first().flavorId == "3"
        assert Application.last().name == "class::app_2"
        assert Application.last().flavorId == "1"

        assert ApplicationConfiguration.count == 3
        assert NodeConfiguration.count == 1

        assert Node.first().configurations.first().name == "config_name"
        assert Application.first().configurations.first().name == "app_config_1"
    }
}
