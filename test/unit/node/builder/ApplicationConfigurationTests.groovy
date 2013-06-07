package node.builder



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(ApplicationConfiguration)
class ApplicationConfigurationTests {

    void testWithoutDescriptions() {
        def params = [name : "Config", value: "Value", application: new Application()]
        def applicationConfiguration= new ApplicationConfiguration(params)
        applicationConfiguration.save()

        assert applicationConfiguration.errors.errorCount == 0
        assert applicationConfiguration.description == null
    }

    void testDescriptions() {
        def params = [name : "Config", value: "Value", description: "Description", application: new Application()]
        def applicationConfiguration= new ApplicationConfiguration(params)
        applicationConfiguration.save()

        assert applicationConfiguration.errors.errorCount == 0
        assert applicationConfiguration.description == "Description"
    }
}
