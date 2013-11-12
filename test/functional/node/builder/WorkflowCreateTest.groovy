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
class WorkflowCreateTest  extends NodeBuilderFunctionalTestBase {
    def tmpDir


    @Before
    void setup(){
        assert Workflow.count == 0
    }

    @Test
    void shouldCreateANewWorkflow(){
        tmpDir = createEmptyRepo()

        login()
        go('workflow')
        assert title == "Workflow List"

        $('.icon-plus-sign').click()
        assert title == "Create Workflow"

        name = "Test"

        def lines = new ClassPathResource("resources/simple_process.xml").getFile().readLines()

        $("textarea", class:'ace_text-input') << lines[0]
        $("textarea", class:'ace_text-input') << lines[1]

        assert processDefinitionKey == "process"
        location = tmpDir.path

        $('#create').click()


        waitFor(0.5){
            assert title.contains("Show")
            assert Workflow.count() == 1
        }
        def workflow2 = Workflow.last()
        assert workflow2.name == "Test"
        assert workflow2.processDefinitionKey == "process"
        assert workflow2.location == tmpDir.path


        $("a[href\$=\"logout/index\"]").click()
        login("gobo", "gobo")
        assert $("#runWorkflow${workflow2.id}").empty

        $("a[href\$=\"logout/index\"]").click()
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

        deleteEmptyRepo()
    }
}
