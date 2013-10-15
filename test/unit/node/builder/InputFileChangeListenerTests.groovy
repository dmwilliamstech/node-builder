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

import grails.test.mixin.Mock
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import node.builder.exceptions.NotDirectoryException
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

@Mock([Node,NodeConfiguration,Application,ApplicationConfiguration])
@TestMixin(ControllerUnitTestMixin)
class InputFileChangeListenerTests {

    void testNewFileWithEmptyApplicationData() {
        Resource resource = new ClassPathResource("resources/empty_application.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Application.count == 0
    }

    @Test
    void shouldLoadNewFileWithSingleApplicationData() {
        def nodeType = new Node(name: "Default", description: "Default Container for nodetype less Applications")
        nodeType.save()

        Resource resource = new ClassPathResource("resources/single_application.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Application.count == 1
        assert Application.first().name == "Some Application"
        assert Application.first().priority == 1
        assert ApplicationConfiguration.count == 1
    }

    @Test
    void shouldLoadNewFileWithSingleNodeData() {
        Resource resource = new ClassPathResource("resources/single_node.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Node.count == 1
        assert Node.first().name == "class::name"
        assert Node.first().flavorId == "small"

        assert Application.count == 2
        assert Application.first().name == "class::app_1"
        assert Application.first().flavorId == "medium"
        assert Application.first().priority == 10
        assert Application.last().name == "class::app_2"
        assert Application.last().flavorId == "tiny"
        assert Application.last().priority == 1

        assert ApplicationConfiguration.count == 2
        assert NodeConfiguration.count == 2

        assert Node.first().configurations.first().name == "config_name"
        assert Node.first().configurations.last().value.class == java.util.ArrayList
        assert Application.last().configurations.first().name == "app_config_3"
        assert Application.last().configurations.last().value.class == java.util.ArrayList
    }

    @Test
    void shouldUpdateExistingConfigurations(){
        Resource resource = new ClassPathResource("resources/single_node.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Node.count == 1
        assert Application.count == 2
        assert ApplicationConfiguration.count == 2
        assert NodeConfiguration.count == 2
        assert Node.first().configurations.first().name == "config_name"
        assert Node.first().configurations.last().value.class == java.util.ArrayList
        assert Application.last().configurations.first().name == "app_config_3"
        assert Application.last().configurations.last().value.class == java.util.ArrayList

        resource = new ClassPathResource("resources/single_node_2.json")
        file = resource.getFile()
        assert file.exists()

        inputFileChangeListener.onNew(resource.file)

        assert Node.count == 1
        assert Application.count == 2

        ApplicationConfiguration.all.each {config ->
            println "configs ${config.name}"
        }
        assert ApplicationConfiguration.count == 2
        NodeConfiguration.all.each {config ->
            println "configs ${config.name}"
        }

        assert NodeConfiguration.count == 2
        assert Node.first().configurations.first().name == "config_name_5"
        assert Node.first().configurations.first().value == "Some Node Value"
        assert Application.last().configurations.first().name == "app_config_3"
        assert Application.last().configurations.first().value == "Some App Value"
    }

    @Test(expected = NotDirectoryException)
    void shouldThrowExceptionIfNotDirectory(){
        InputFileChangeListener.getDefaultListener(File.createTempFile("not",".dir"))
    }
}
