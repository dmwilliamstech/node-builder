package node.builder

import com.sun.jna.platform.win32.SetupApi
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Project)
@Mock([ProjectType])
class ProjectTests {
    def projectType

    @Before
    void setup(){
        projectType = new ProjectType(name: "GIT Repository")
        projectType.save()
        assert projectType.errors.errorCount == 0
    }

    @Test
    void shouldValidateName(){
        def project = new Project(name:"",
                bpmn: "<xml></xml>",
                description: "some description",
                location: "git@github.com:OpenDX/node-builder.git",
                active: true,
                projectType: projectType,
                processDefinitionKey: "somekey"
        )
        project.save()
        assert project.errors.errorCount == 1

        project.name = "Some Name"
        project.save()
        assert project.errors.errorCount == 0

        project = new Project(name:"Some Name",
                bpmn: "<xml></xml>",
                description: "some description",
                location: "git@github.com:OpenDX/node-builder.git",
                active: true,
                projectType: projectType,
                processDefinitionKey: "somekey"
        )
        project.save()
        assert project.errors.errorCount == 1

        project.name = "Some Name Part 2"
        project.save()
        assert project.errors.errorCount == 0
    }

    @Test
    void shouldValidateLocation(){
        def project = new Project(name:"Some Name",
                bpmn: "<xml></xml>",
                description: "some description",
                location: "not???**^^^### a url",
                active: true,
                projectType: projectType,
                processDefinitionKey: "somekey"
        )
        project.validate()
//        project.save()
        assert project.errors.errorCount == 1

        project.location = "git@github.com:OpenDX/node-builder.git"
        project.save()
        assert project.errors.errorCount == 0

        project.location = "https://github.com/OpenDX/node-builder.git"
        project.save()
        assert project.errors.errorCount == 0

        project.location = "https://192.168.1.100/user/project.git"
        project.save()
        assert project.errors.errorCount == 1
    }

    @After
    void teardown(){
    }

}
