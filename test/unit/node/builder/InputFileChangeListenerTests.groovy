package node.builder



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@Mock(Application)
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
    }
}
