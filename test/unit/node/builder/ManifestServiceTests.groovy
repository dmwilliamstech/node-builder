package node.builder

import grails.test.mixin.TestFor

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ManifestService)
class ManifestServiceTests {

    void testFormating() {
        def service = new ManifestService()
        def test = service.processConfigurationValue("hello")
        assert test.toString() == "\"hello\""
        test = service.processConfigurationValue(["hello", "goodbye"])
        assert test.equals("[ \"hello\", \"goodbye\" ]")
    }
}
