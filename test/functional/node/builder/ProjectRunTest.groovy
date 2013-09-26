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

//@Mixin(Retryable)
class ProjectRunTest extends NodeBuilderFunctionalTestBase {
    def tmpDir
    def project

    @Before
    void setup(){
        tmpDir = createEmptyRepo()

        project = new Project()
        project.name = "Test"
        project.description = "Test"
        project.bpmn = new ClassPathResource("resources/monitor_git.bpmn20.xml").getFile().text
        project.active = false
        project.location = tmpDir.path
        project.processDefinitionKey = "gitChangeMonitor"
        project.projectType = (ProjectType.findByName("GIT Repository"))

        project.save(failOnError: true)

        assert Project.count == 1
    }

    @Test
    void shouldRunANewProject(){
        login()
        go('project')
        assert title == "Project List"

        $("#runProject${project.id}").click()
        sleep(1000)
        assert $("#projectState${project.id}").text() == ProjectState.RUNNING.name()
        assert $(".alert").text() == "Running process gitChangeMonitor on project Test"
        sleep(5000)
        $("a[href\$=\"project\"]").click()
        assert $("#projectState${project.id}").text() == ProjectState.OK.name()
    }

    @After
    void tearDown(){
        Project.where { id > 0l }.deleteAll()
        assert Project.count == 0

        deleteEmptyRepo()
    }
}
