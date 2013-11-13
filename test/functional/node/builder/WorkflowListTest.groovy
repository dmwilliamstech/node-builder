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

class WorkflowListTest extends NodeBuilderFunctionalTestBase{

    @Before
    void setup(){
        Config.globalConfig.put("application.footer.text.value", "Copyright &copy; 2013 Some Company")
        (new WorkflowCreateTest()).shouldCreateANewWorkflow()
    }

    @Test
    void shouldDisplayWorkflowList(){
        login()
        go('workflow')
        assert title == "Workflow List"
        waitFor(0.1){
            assert $('[id^=workflowShow]').first().text() == ("Test")
            assert $('.icon-pencil') != null
            assert $('#footerCopyrightText').first().text() == ("Copyright © 2013 Some Company")
        }
    }

    @After
    void teardown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Workflow.all.each{workflow ->
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_TAGS po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_VARIABLES po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_ORGANIZATIONS po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
        }
        Workflow.where {id>0l}.deleteAll()
        deleteEmptyRepo()
    }
}
