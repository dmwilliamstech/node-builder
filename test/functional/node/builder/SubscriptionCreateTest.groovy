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


class SubscriptionCreateTest extends NodeBuilderFunctionalTestBase {

    @Before
    void setup(){
        assert Subscription.count == 0

        def workflow = Workflow.findOrCreateByName("Simple Workflow")
        workflow.state = WorkflowState.OK
        workflow.organizations = ["fraggles"]
        workflow.active = false
        workflow.subscribable = true
        workflow.location = '/tmp'
        workflow.workflowType = WorkflowType.findByName(WorkflowTypeEnum.FOLDER_MONITOR.name)
        workflow.description = "Sample Workflow"
        workflow.message = "Sample Workflow created OK"
        workflow.tags = [WorkflowTag.findByName("Postgresql")]
        workflow.bpmn = new ClassPathResource("resources/sample_process.bpmn20.xml").getFile().text
        workflow.processDefinitionKey = "groovyProcess"
        workflow.variables = ["variable1","variable2"]
        workflow.save(failOnError: true, flush: true)

        assert Workflow.count == 1
    }

    @Test
    void shouldCreateANewSubscription(){
        login()
        go('subscription')
        assert title == "Subscription List"

        $('.icon-plus-sign').click()
        assert title == "Create Subscription"


        waitFor(10, 1){
            $("#variable1") << "test 1"
            $("#variable2") << "test 1"
        }

        $("#create").click()


        waitFor(0.5){
            assert title.contains("Subscription List")
            assert Subscription.count() == 1
            assert !$("a[href\$='edit/${Subscription.last().id}']").empty
        }
        def subscription2 = Subscription.last()

        assert subscription2.variables

        assert subscription2 != null

        logout()
        login("gobo", "gobo")
        assert $("a[href\$='edit/${Subscription.last().id}']").empty

        logout()
    }

    @After
    void tearDown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Subscription.all.each{subscription ->
            sessionFactory.currentSession.createSQLQuery("delete from SUBSCRIPTION_VARIABLE po where po.SUBSCRIPTION_ID = ${subscription.id}").executeUpdate()

        }
        Subscription.where {id>0l}.deleteAll()

        Workflow.all.each{workflow ->
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_VARIABLES po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_TAGS po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_ORGANIZATIONS po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
        }
        Workflow.where {id>0l}.deleteAll()

        deleteEmptyRepo()
    }
}
