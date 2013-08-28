package node.builder

import geb.junit4.GebReportingTest
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.springframework.core.io.ClassPathResource

class ProjectListTest extends GebReportingTest {

    @Before
    void setup(){
        def project = new Project()
        project.name = "Test"
        project.description = "Test"
        project.bpmn = new ClassPathResource("resources/process.xml").getFile().text
        project.active = false
        project.location = "git@github.com:kellyp/testy.git"
        project.processDefinitionKey = "gitChangeMonitor"
        project.projectType = (ProjectType.findByName("GIT Repository"))

        project.save(failOnError: true)

        assert Project.count == 1
    }

    void testProjectList(){
        login()
        go('project')
        assert title == "Project List"
        sleep(10000)
        assert $('#project1').first().text() == ("Test")
        assert $('.icon-pencil') != null
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
        Project.where { id > 0l }.deleteAll()
        assert Project.all.size() == 0
    }
}
