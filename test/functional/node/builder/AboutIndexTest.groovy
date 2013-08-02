package node.builder

import geb.junit4.GebReportingTest
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.springframework.core.io.ClassPathResource

class AboutIndexTest extends GebReportingTest {
    def version
    def reference
    def dependencies

    @Before
    void setup(){
        def string = new File('application.properties').text
        def matcher = string =~ /app.version=(.*)/
        version = matcher[0][1]

        reference = this.class.classLoader.getResourceAsStream("reference.txt").text.replaceAll(/\s/, '')
        dependencies = new XmlSlurper().parseText(this.class.classLoader.getResourceAsStream("dependency-report.xml").text)

        assert version != null
        assert reference != null
        assert dependencies != null
    }

    void testIndex(){
        login()

        go('about')

        assert title == 'About'
        assert $('#application-name').text() == 'node-builder'
        assert $('#application-version').text().contains(version)
        assert $('#application-reference').text().replaceAll(/Reference\s/, "") == reference

        assert $('#list-dependency-build') != null
        assert $('#list-dependency-provided') != null
    }

    void login(){
        go('login/auth')
        assert title == "Login"

        j_username = "admin"
        j_password = "admin"
        $('input#submit').click()
    }
}
