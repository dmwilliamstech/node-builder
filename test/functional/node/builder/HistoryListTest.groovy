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
    def workflow

    @Before
    void setup(){
        MetricsTestHelper.resetMetrics()

        assert Workflow.count == 0
        (new WorkflowCreateTest()).shouldCreateANewWorkflow()
        assert Workflow.count == 1
        workflow = Workflow.first()
        workflow.bpmn = new ClassPathResource("resources/groovy_task.bpmn20.xml").getFile().text
        workflow.processDefinitionKey = "gitChangeMonitor"
        workflow.location = "https://github.com/kellyp/testy.git"
        workflow.save(flush: true)
    }

    @Test
    void shouldDisplayAListOfResults(){
        (new WorkflowRunTest()).shouldRunANewWorkflow()

        $("a[href\$=\"workflow/history/${workflow.id}\"]").click()
        assert $("table").size() == 2 //1 + the summary table

        workflow = Workflow.first()
        workflow.bpmn = new ClassPathResource("resources/groovy_task.bpmn20.xml").getFile().text.replaceAll('true', 'false')
        workflow.save()

        logout()

        (new WorkflowRunTest()).shouldRunANewWorkflow()

        $("a[href\$=\"workflow/history/${workflow.id}\"]").click()
        assert $("table").size() == 2 //1 + the summary table

        workflow = Workflow.first()
        workflow.bpmn = new ClassPathResource("resources/groovy_task.bpmn20.xml").getFile().text
        workflow.save()

        logout()

        (new WorkflowRunTest()).shouldRunANewWorkflow()

        $("a[href\$=\"workflow/history/${workflow.id}\"]").click()
        assert $("table").size() == 3 //2 + the summary table
    }

    @After
    void tearDown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Workflow.all.each{workflow ->
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_VARIABLES po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_ORGANIZATIONS po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
        }
        Workflow.where {id>0l}.deleteAll()
//        "rm -rf /tmp/history_test".execute()
        deleteEmptyRepo()
    }
}
