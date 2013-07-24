package node.builder

import geb.junit4.GebReportingTest
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.springframework.core.io.ClassPathResource

class ManifestListTest extends GebReportingTest {

    @Before
    void setup(){
        def manifest = new Manifest()
        manifest.name = "Test"
        manifest.description = "Test"
        manifest.manifest = (new JsonSlurper()).parseText((new ClassPathResource("resources/manifest.json").getFile().text))
        manifest.save(failOnError: true)

        assert Manifest.all.size() == 1
    }

    void testManifestList(){
        login()
        assert title == "Manifest List"
        assert $('h4').first().text() == "Test"
        assert $('.icon-pencil') != null
        assert $('.icon-upload') != null
        assert $('.icon-remove-sign') != null
        assert $('.icon-info-sign') != null
    }

    void testManifestNew(){
        login()
        assert title == "Manifest List"
        $('i.icon-plus-sign').click()
        assert $('h4').first().text() == "Manifest Name"
    }

    void login(){
        go('login/auth')
        assert title == "Login"

        j_username = "admin"
        j_password = "admin"
        $('input#submit').click()
    }
    @After
    void teardown(){
        Manifest.where { id > 0l }.deleteAll()
        assert Manifest.all.size() == 0
    }
}
