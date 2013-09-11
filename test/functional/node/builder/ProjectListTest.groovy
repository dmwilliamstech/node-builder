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

import geb.junit4.GebReportingTest
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.core.io.ClassPathResource

class ProjectListTest extends NodeBuilderFunctionalTest{

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

    @Test
    void shouldDisplayProjectList(){
        login()
        go('project')
        assert title == "Project List"
        sleep(10000)
        assert $('[id^=project]').first().text() == ("Test")
        assert $('.icon-pencil') != null
    }

    @After
    void teardown(){
        Project.where { id > 0l }.deleteAll()
        assert Project.count == 0
    }
}
