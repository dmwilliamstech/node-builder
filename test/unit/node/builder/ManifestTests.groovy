package node.builder

import grails.converters.JSON
import grails.test.mixin.*
import grails.test.mixin.web.ControllerUnitTestMixin
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Manifest)
@TestMixin(ControllerUnitTestMixin)
class ManifestTests {

    void testStringifying() {
        def manifest = new Manifest('{"name":"bob"}')
        manifest.save()

        assert Manifest.first().manifest.name == "bob"
    }

    void testParsing() {
        def json = JSON.parse('{"name":"bob"}')

        def manifest = new Manifest()
        manifest.setManifest(json)
        manifest.save()

        assert Manifest.count == 1

        assert Manifest.first().manifest.name == "bob"
    }
}
