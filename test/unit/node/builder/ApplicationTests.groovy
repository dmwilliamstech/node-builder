package node.builder



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Application)
class ApplicationTests {

    void testDefaultNodeType() {
        def application = new Application(name: "Some Application", description: "Some Description")
       assert application.node == null
    }

}
