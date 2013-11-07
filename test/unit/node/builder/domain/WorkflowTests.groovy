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

package node.builder.domain

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import node.builder.Workflow
import node.builder.WorkflowType
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Workflow)
@Mock([WorkflowType])
class WorkflowTests {
    def workflowType

    @Before
    void setup(){
        workflowType = new WorkflowType(name: "GIT Repository")
        workflowType.save()
        assert workflowType.errors.errorCount == 0
    }

    @Ignore
    @Test
    void shouldValidateName(){
        def workflow = new Workflow(name:"",
                bpmn: new ClassPathResource("resources/process.xml").getFile().text,
                description: "some description",
                location: "git@github.com:OpenDX/node-builder.git",
                active: true,
                workflowType: workflowType,
                processDefinitionKey: "gitChangeMonitor"
        )
        workflow.save()
        assert workflow.errors.errorCount == 1

        workflow.name = "Some Name"
        workflow.save()
        assert workflow.errors.errorCount == 0

        workflow = new Workflow(name:"Some Name",
                bpmn: new ClassPathResource("resources/process.xml").getFile().text,
                description: "some description",
                location: "git@github.com:OpenDX/node-builder.git",
                active: true,
                workflowType: workflowType,
                processDefinitionKey: "gitChangeMonitor"
        )
        workflow.save()
        assert workflow.errors.errorCount == 1

        workflow.name = "Some Name Part 2"
        workflow.save()
        assert workflow.errors.errorCount == 0
    }

    @Test
    void shouldValidateLocation(){
        def workflow = new Workflow(name:"Some Name",
                bpmn: new ClassPathResource("resources/process.xml").getFile().text,
                description: "some description",
                location: "not???**^^^### a url",
                active: true,
                workflowType: workflowType,
                processDefinitionKey: "gitChangeMonitor"
        )
        workflow.validate()
        assert workflow.errors.errorCount == 1

        workflow.location = "git@github.com:OpenDX/node-builder.git"
        workflow.save()
        assert workflow.errors.errorCount == 0

        workflow.location = "https://github.com/OpenDX/node-builder.git"
        workflow.save()
        assert workflow.errors.errorCount == 0

        createEmptyRepo()
        workflow.location = "/tmp/dir"
        workflow.save()
        assert workflow.errors.errorCount == 0

        workflow.location = "https://192.168.1.100/user/workflow.git"
        workflow.save()
        assert workflow.errors.errorCount == 1
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
