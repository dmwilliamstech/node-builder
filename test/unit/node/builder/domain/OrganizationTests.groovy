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
import node.builder.Project
import node.builder.ProjectType
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Project)
@Mock([Project, ProjectType])
class OrganizationTests {

    @Test
    void shouldHaveAssociationWithProjects() {
        def project = new Project(
                name: "test",
                location: "blah",
                projectType: (new ProjectType(name: "Test Type")).save(validate: false),
                bpmn: "<bpmn></bpmn>",
                processDefinitionKey: "key"
        )

        project.save(validate:  false)
        assert !project.hasErrors()

        project.addToOrganizations("Testers")
        project.save(validate:  false)
        assert !project.hasErrors()
        assert Project.first().organizations.first() == "Testers"

        def projects = Project.findAllByOrganizations(["Testers"])
        assert projects.size() == 1

        project.addToOrganizations("Users")
        project.save(validate:  false)

        projects = Project.findAllByOrganizations(["Testers"])
        assert projects.size() == 1

        projects = Project.findAllByOrganizations(new TreeSet<String>(["Testers","Users"]))
        assert projects.size() == 1

        projects = Project.findAllByOrganizations(["Admins"])
        assert projects.size() == 0

        projects = Project.findAllByOrganizations(new TreeSet<String>(["Users","Testers"]))
        assert projects.size() == 1

        project.delete()
        assert Project.count == 0
    }
}
