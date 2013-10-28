package node.builder

import node.builder.metrics.MetricsTestHelper
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.hibernate.SessionFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.context.ApplicationContext
import org.springframework.core.io.ClassPathResource

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
class HistoryListTest extends NodeBuilderFunctionalTestBase{
    def project

    @Before
    void setup(){
        MetricsTestHelper.resetMetrics()

        assert Project.count == 0
        (new ProjectCreateTest()).shouldCreateANewProject()
        assert Project.count == 1
        project = Project.first()
        project.bpmn = new ClassPathResource("resources/groovy_task.bpmn20.xml").getFile().text
        project.processDefinitionKey = "gitChangeMonitor"
        project.location = "https://github.com/kellyp/testy.git"
        project.save(flush: true)
    }

    @Test
    void shouldDisplayAListOfResults(){
        (new ProjectRunTest()).shouldRunANewProject()

        $("a[href\$=\"project/history/${project.id}\"]").click()
        assert $("table").size() == 2 //1 + the summary table

        project = Project.first()
        project.bpmn = new ClassPathResource("resources/groovy_task.bpmn20.xml").getFile().text.replaceAll('true', 'false')
        project.save()

        logout()

        (new ProjectRunTest()).shouldRunANewProject()

        $("a[href\$=\"project/history/${project.id}\"]").click()
        assert $("table").size() == 2 //1 + the summary table

        project = Project.first()
        project.bpmn = new ClassPathResource("resources/groovy_task.bpmn20.xml").getFile().text
        project.save()

        logout()

        (new ProjectRunTest()).shouldRunANewProject()

        $("a[href\$=\"project/history/${project.id}\"]").click()
        assert $("table").size() == 3 //2 + the summary table
    }

    @After
    void tearDown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Project.all.each{project ->
            sessionFactory.currentSession.createSQLQuery("delete from PROJECT_ORGANIZATIONS po where po.PROJECT_ID = ${project.id}").executeUpdate()
        }
        Project.where {id>0l}.deleteAll()
//        "rm -rf /tmp/history_test".execute()
        deleteEmptyRepo()
    }
}
