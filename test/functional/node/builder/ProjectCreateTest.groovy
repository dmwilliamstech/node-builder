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

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.hibernate.SessionFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ClassPathResource

//@Mixin(Retryable)
class ProjectCreateTest  extends NodeBuilderFunctionalTestBase {
    def tmpDir


    @Before
    void setup(){
        assert Project.count == 0
    }

    @Test
    void shouldCreateANewProject(){
        tmpDir = createEmptyRepo()

        login()
        go('project')
        assert title == "Project List"

        $('.icon-plus-sign').click()
        assert title == "Create Project"

        name = "Test"

        def lines = new ClassPathResource("resources/simple_process.xml").getFile().readLines()

        $("textarea", class:'ace_text-input') << lines[0]
        $("textarea", class:'ace_text-input') << lines[1]

        sleep(500)
        assert processDefinitionKey == "process"
        location = tmpDir.path

        $('#create').click()

        sleep(500)

        assert title.contains("Show")
        assert Project.count() == 1

        def project2 = Project.last()
        assert project2.name == "Test"
        assert project2.processDefinitionKey == "process"
        assert project2.location == tmpDir.path


        $("a[href\$=\"logout/index\"]").click()
        login("gobo", "gobo")
        assert $("#runProject${project2.id}").empty

        $("a[href\$=\"logout/index\"]").click()
    }

    @After
    void tearDown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Project.all.each{project ->
            sessionFactory.currentSession.createSQLQuery("delete from PROJECT_ORGANIZATIONS po where po.PROJECT_ID = ${project.id}").executeUpdate()
        }
        Project.where {id>0l}.deleteAll()

        deleteEmptyRepo()
    }
}
