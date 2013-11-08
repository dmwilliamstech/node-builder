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
class WorkflowRunTest extends NodeBuilderFunctionalTestBase {

    @Before
    void setup(){
        assert Workflow.count == 0
        (new WorkflowCreateTest()).shouldCreateANewWorkflow()
        assert Workflow.count == 1
        def workflow = Workflow.first()
        workflow.bpmn = new ClassPathResource("resources/monitor_git.bpmn20.xml").getFile().text
        workflow.processDefinitionKey = "gitChangeMonitor"
        workflow.save(flush: true)

    }

    @Test
    void shouldRunANewWorkflow(){
        login()
        go('workflow')
        assert title == "Workflow List"
        def workflow = Workflow.first()

        $("#runWorkflow${workflow.id}").click()
        this.waitFor(10,1){
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.RUNNING.color)
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.RUNNING.icon)
            assert $(".alert").text() == "Running process gitChangeMonitor on workflow Test"
        }

        this.waitFor(20, 1){
            $("a[href\$=\"workflow\"]").click()
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.OK.color)
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.OK.icon)
        }
    }

    @Test
    void shouldRunANewWorkflowWithUserTasks(){
        login()
        go('workflow')
        assert title == "Workflow List"
        def workflow = Workflow.first()
        workflow.bpmn = new ClassPathResource("resources/human_task.bpmn20.xml").getFile().text
        workflow.processDefinitionKey = "gitChangeMonitor"
        workflow.save(flush: true)



        $("#runWorkflow${workflow.id}").click()
        this.waitFor(10,1){
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.RUNNING.color)
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.RUNNING.icon)
            assert $(".alert").text() == "Running process gitChangeMonitor on workflow Test"
        }

        this.waitFor(20, 1){
            $("a[href\$=\"workflow\"]").click()
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.WAITING.color)
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.WAITING.icon)
        }

        //accept the first task
        $("#completeTaskAccept${workflow.id}").click()

        sleep(5000)
        $("#completeTaskButton").click()

        this.waitFor(30, 1){
            $("a[href\$=\"workflow\"]").click()
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.OK.color)
            assert $("#workflowState${workflow.id} h2 i").classes().contains(WorkflowState.OK.icon)
        }
    }

    @Test
    void shouldRunANewWorkflowWithUserTasksFromShow(){
        login()
        go('workflow')
        assert title == "Workflow List"
        def workflow = Workflow.first()
        workflow.bpmn = new ClassPathResource("resources/human_task.bpmn20.xml").getFile().text
        workflow.processDefinitionKey = "gitChangeMonitor"
        workflow.save(flush: true)


        $("#workflowShow${workflow.id}").click()
        $("#runWorkflow").click()
        this.waitFor(10,1){
            assert $("#workflowState h2 i").classes().contains(WorkflowState.RUNNING.color)
            assert $("#workflowState h2 i").classes().contains(WorkflowState.RUNNING.icon)
            assert $(".alert").text() == "Running process gitChangeMonitor on workflow Test"
        }

        this.waitFor(20, 1){
            driver.navigate().refresh()
            assert $("#workflowState h2 i").classes().contains(WorkflowState.WAITING.color)
            assert $("#workflowState h2 i").classes().contains(WorkflowState.WAITING.icon)
            assert $("#workflowMessage").text() == "Process ${workflow.processDefinitionKey} on workflow ${workflow.name} waiting on some name"
        }

        //accept the first task
        $("#completeTaskAccept").click()

        sleep(5000)
        $("#completeTaskButton").click()

        this.waitFor(30, 1){
            driver.navigate().refresh()
            assert $("#workflowState h2 i").classes().contains(WorkflowState.OK.color)
            assert $("#workflowState h2 i").classes().contains(WorkflowState.OK.icon)
            assert $("#workflowMessage").text() == "Process gitChangeMonitor on workflow Test finished - It's even groovier man"
        }
    }

    @After
    void tearDown(){
        ApplicationContext context = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT);
        SessionFactory sessionFactory = context.getBean('sessionFactory')

        Workflow.all.each{workflow ->
            sessionFactory.currentSession.createSQLQuery("delete from WORKFLOW_ORGANIZATIONS po where po.WORKFLOW_ID = ${workflow.id}").executeUpdate()
        }
        Workflow.where {id>0l}.deleteAll()
        deleteEmptyRepo()
    }
}
