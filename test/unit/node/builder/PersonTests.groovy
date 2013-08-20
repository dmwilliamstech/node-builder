package node.builder



import grails.test.mixin.TestFor
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Person)
class PersonTests {

    @Test
    void shouldSaveToMongoDB() {
        def person = new Person(name: "Hank")
        person.save()

        assert Person.first().name == "Hank"
    }
}
