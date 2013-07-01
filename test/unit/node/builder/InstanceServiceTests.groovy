package node.builder



import grails.test.mixin.*
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(InstanceService)
@Mock([Instance,Image,Flavor])
@TestMixin(ControllerUnitTestMixin)
class InstanceServiceTests {

    void testGetInstances() {
        InstanceService service = new InstanceService()
        service.loadInstances(OpenStackTestConnection.createConnection("resources/servers.json"))
        assert Instance.count == 2
    }

    void testUpdateInstances() {
        InstanceService service = new InstanceService()
        service.loadInstances(OpenStackTestConnection.createConnection("resources/servers.json"))
        assert Instance.count == 2

        service.loadInstances(OpenStackTestConnection.createConnection("resources/empty_array.json"))
        assert Instance.count == 0
    }
}
