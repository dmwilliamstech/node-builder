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

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class ProjectCreateTest  extends NodeBuilderFunctionalTestBase {
    def tmpDir

    @Before
    void setup(){
        tmpDir = createEmptyRepo()

        def project = new Project()
        project.name = "Test"
        project.description = "Test"
        project.bpmn = new ClassPathResource("resources/process.xml").getFile().text
        project.active = false
        project.location = tmpDir.path
        project.processDefinitionKey = "gitChangeMonitor"
        project.projectType = (ProjectType.findByName("GIT Repository"))

        project.save(failOnError: true)

        assert Project.count == 1
    }

    @Test
    void shouldCreateANewProject(){
        login()
        go('project')
        assert title == "Project List"

        $('.icon-plus-sign').click()
        assert title == "Create Project"

        name = "Test 2"
        $('.ace_text-input').value(new ClassPathResource("resources/process.xml").getFile().text)
        sleep(5000)
        report("project create form")
        assert processDefinitionKey == "gitChangeMonitor"
        location = tmpDir.path

        $('#create').click()

        sleep(500)

        assert Project.count() == 2
        def project2 = Project.last()
        assert project2.name == "Test 2"
        assert project2.processDefinitionKey == "gitChangeMonitor"
        assert project2.location == tmpDir.path
    }

    @After
    void tearDown(){
        Project.where { id > 0l }.deleteAll()
        assert Project.count == 0

        deleteEmptyRepo()
    }
}