/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package node.builder

import com.sun.jna.platform.win32.SetupApi
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.After
import org.junit.Before
import org.junit.Ignore
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

    @Ignore
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
        assert project.errors.errorCount == 1

        project.location = "git@github.com:OpenDX/node-builder.git"
        project.save()
        assert project.errors.errorCount == 0

        project.location = "https://github.com/OpenDX/node-builder.git"
        project.save()
        assert project.errors.errorCount == 0

        createEmptyRepo()
        project.location = "/tmp/dir"
        project.save()
        assert project.errors.errorCount == 0

        project.location = "https://192.168.1.100/user/project.git"
        project.save()
        assert project.errors.errorCount == 1
    }

    @After
    void teardown(){
        deleteEmptyRepo()
    }

    private createEmptyRepo(){
        def tempDir = new File("/tmp/dir")
        if(tempDir.exists())
            tempDir.deleteDir()
        tempDir.mkdir()

        def env = [""]
        def gitInit = "git init".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        def touch = "touch test".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        def gitAdd = "git add test".execute([], tempDir)
        gitInit.waitForOrKill(1000)

        def gitCommit = "git commit -m 'test' -a".execute([], tempDir)
        gitInit.waitForOrKill(1000)
    }

    private deleteEmptyRepo(){
        def tempDir = new File("/tmp/dir")
        if(tempDir.exists())
            tempDir.deleteDir()
    }
}
