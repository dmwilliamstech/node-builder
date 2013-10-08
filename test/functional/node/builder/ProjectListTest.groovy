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

class ProjectListTest extends NodeBuilderFunctionalTestBase{

    @Before
    void setup(){
        (new ProjectCreateTest()).shouldCreateANewProject()
    }

    @Test
    void shouldDisplayProjectList(){
        login()
        go('project')
        assert title == "Project List"
        sleep(100)
        assert $('[id^=projectShow]').first().text() == ("Test")
        assert $('.icon-pencil') != null
    }

    @After
    void teardown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Project.all.each{project ->
            sessionFactory.currentSession.createSQLQuery("delete from PROJECT_ORGANIZATIONS po where po.PROJECT_ID = ${project.id}").executeUpdate()
        }
        Project.where {id>0l}.deleteAll()
        deleteEmptyRepo()
    }
}
