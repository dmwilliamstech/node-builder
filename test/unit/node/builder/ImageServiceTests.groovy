package node.builder



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ImageService)
@Mock(Image)
class ImageServiceTests {

    void testGetImages() {
        ImageService service = new ImageService()
        service.loadImages(OpenStackTestConnection.createConnection("resources/images.json"))
        assert Image.count == 2
    }

    void testUpdateImages() {
        ImageService service = new ImageService()
        service.loadImages(OpenStackTestConnection.createConnection("resources/images.json"))
        assert Image.count == 2

        service.loadImages(OpenStackTestConnection.createConnection("resources/empty_array.json"))
        assert Image.count == 0
    }
}
