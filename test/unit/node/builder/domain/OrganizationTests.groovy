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

package node.builder.domain

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import node.builder.Workflow
import node.builder.WorkflowType
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Workflow)
@Mock([Workflow, WorkflowType])
class OrganizationTests {

    @Test
    void shouldHaveAssociationWithWorkflows() {
        def workflow = new Workflow(
                name: "test",
                location: "blah",
                workflowType: (new WorkflowType(name: "Test Type")).save(validate: false),
                bpmn: "<bpmn></bpmn>",
                processDefinitionKey: "key"
        )

        workflow.save(validate:  false)
        assert !workflow.hasErrors()

        workflow.addToOrganizations("Testers")
        workflow.save(validate:  false)
        assert !workflow.hasErrors()
        assert Workflow.first().organizations.first() == "Testers"

        def workflows = Workflow.findAllByOrganizations(["Testers"])
        assert workflows.size() == 1

        workflow.addToOrganizations("Users")
        workflow.save(validate:  false)

        workflows = Workflow.findAllByOrganizations(["Testers"])
        assert workflows.size() == 1

        workflows = Workflow.findAllByOrganizations(new TreeSet<String>(["Testers","Users"]))
        assert workflows.size() == 1

        workflows = Workflow.findAllByOrganizations(["Admins"])
        assert workflows.size() == 0

        workflows = Workflow.findAllByOrganizations(new TreeSet<String>(["Users","Testers"]))
        assert workflows.size() == 1

        workflow.delete()
        assert Workflow.count == 0
    }
}
