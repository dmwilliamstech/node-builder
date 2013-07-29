package node.builder

import grails.converters.JSON
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Manifest)
@TestMixin(ControllerUnitTestMixin)
class ManifestTests {

    void testStringifying() {
        def manifest = new Manifest('{"name":"bob"}')
        manifest.name = "Test"
        manifest.save()

        assert Manifest.first().manifest.name == "bob"
    }

    void testParsing() {
        def json = JSON.parse('{"name":"bob"}')

        def manifest = new Manifest()
        manifest.setManifest(json)
        manifest.name = "Test2"
        manifest.save()

        assert Manifest.count == 1

        assert Manifest.first().manifest.name == "bob"
    }
}
