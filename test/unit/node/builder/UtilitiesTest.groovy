package node.builder

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.Before

@TestFor(Manifest)
@TestMixin(ControllerUnitTestMixin)
@Mock([Deployment, Instance])
class UtilitiesTest {
    @Before
    public void setUp() {
        grailsApplication.config.something = "something"
    }

    def testSerialize(){
        def utilities = new Utilities()
        assert utilities.isSerializable("true") == true
        def map = new HashMap()
        map.put("hi", "hello")

        assert utilities.isSerializable(map) == true
        assert utilities.isSerializable(new Manifest(name: "test")) == false
    }

    def testSerializeDomain(){
        def utilities = new Utilities()
        def manifest = new Manifest()
        utilities.grailsApplication = grailsApplication

        assert manifest != null
        manifest.name = "test"
        manifest.save()

        def map = utilities.serializeDomain(manifest)
        assert map.name == "test"
        assert map instanceof Map

        def instance = new Instance(name: "instance")

        def deployment = new Deployment(manifest: manifest, instances: [instance])

        map = utilities.serializeDomain(deployment)

        assert map.manifest.name == "test"
        assert map.instances.first().name == "instance"
    }
}
