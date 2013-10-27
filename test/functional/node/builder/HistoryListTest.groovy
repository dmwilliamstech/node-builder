package node.builder

import com.mongodb.DBObject
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
        def process = "git clone https://github.com/kellyp/testy.git /tmp/history_testy".execute()
        assert process.waitFor() == 0

        process = "git clone /tmp/history_testy history_testy".execute()
        assert process.waitFor() == 0

        process = "git clone /tmp/history_testy history_testy2".execute()
        assert process.waitFor() == 0

        assert Project.count == 0
        (new ProjectCreateTest()).shouldCreateANewProject()
        assert Project.count == 1
        project = Project.first()
        project.bpmn = new ClassPathResource("resources/monitor_git.bpmn20.xml").getFile().text
        project.processDefinitionKey = "gitChangeMonitor"
        project.location = "history_testy"
        project.save(flush: true, failOnError: true)
    }


    @Test
    void shouldDisplayAListOfResults(){




        def process = "history_testy2/update_me.sh".execute()
        assert process.waitFor() == 0

        (new ProjectRunTest()).shouldRunANewProject()

        def metrics = (new MetricService()).metricsForProject(project.name)
        Iterable<DBObject> workflows = metrics.workflows
        assert workflows.iterator().size() == 1

        logout()

        (new ProjectRunTest()).shouldRunANewProject()

        metrics = (new MetricService()).metricsForProject(project.name)
        workflows = metrics.workflows
        assert workflows.iterator().size() == 1

        process = "history_testy2/update_me.sh".execute()
        assert process.waitFor() == 0

        logout()

        (new ProjectRunTest()).shouldRunANewProject()

        metrics = (new MetricService()).metricsForProject(project.name)
        workflows = metrics.workflows
        assert workflows.iterator().size() == 2
    }

    @After
    void tearDown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Project.all.each{project ->
            sessionFactory.currentSession.createSQLQuery("delete from PROJECT_ORGANIZATIONS po where po.PROJECT_ID = ${project.id}").executeUpdate()
        }
        Project.where {id>0l}.deleteAll()
//        "rm -rf /tmp/history_testy".execute()
//        "rm -rf history_testy".execute()
        deleteEmptyRepo()
    }
}
