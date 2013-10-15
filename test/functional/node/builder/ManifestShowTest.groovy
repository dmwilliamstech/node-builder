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

import geb.junit4.GebReportingTest
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource


class ManifestShowTest extends GebReportingTest {
    @BeforeClass
    static void cleanData(){
//        Manifest.where { id > 0l }.deleteAll()
//        ApplicationConfiguration.where { id > 0l }.deleteAll()
//        NodeConfiguration.where { id > 0l }.deleteAll()
//        Application.where { id > 0l }.deleteAll()
//        Node.where { id > 0l }.deleteAll()
    }

    @Before
    void setup() {
        Resource resource = new ClassPathResource("resources/single_node.json")
        def file = resource.getFile()
        assert file.exists()

        def inputFileChangeListener = new InputFileChangeListener()
        inputFileChangeListener.onNew(resource.file)

        assert Node.count == 2
        assert Application.count == 2
        assert Manifest.count == 0
        (new ManifestListTest()).login()
    }

    def testNewManifest() {

        assert title == "Manifest List"

        $('i.icon-plus-sign').click()

        assert title == "Node Builder"

        $('#manifestName').value("Test 2")
        assert $('#manifestName').value() == "Test 2"

        $('#saveManifestButton').click()
        assert $('div.alert-error').text().contains('Not much point in proceeding without an instance, is there?')

        createInstanceAddNode("test1", "#node2")

        waitFor(10,1){
            assert $('#edit_0_2_applications').size() > 0
            $('#edit_0_2_applications').click()
            $('#input_0_0_2_0').value("Some New Values")

            $('#input_0_0_2_0').value() == "Some New Values"
        }
        createInstanceAddNode("test2", "#node2")

        waitFor(10,1){
            assert $('#edit_1_2_applications').size() > 0
            $('#edit_1_2_applications').click()
        }

        assert ($('#input_0_1_2_0').value() == "Some Values" || $('#input_1_1_2_0').value() == "Some Values" )

        $('#saveManifestButton').click()
        sleep(5000)
        assert Manifest.count == 1

    }

    def createInstanceAddNode(instance, node) {
        $('i.icon-plus-sign').click()
        waitFor(1){
            $('#newInstanceName').value(instance)
        }

        assert $(node).size() == 1
        $(node).click()

        $('button.btn-primary').click()
    }

    @After
    void teardown() {
        Manifest.where { id > 0l }.deleteAll()
        ApplicationConfiguration.where { id > 0l }.deleteAll()
        NodeConfiguration.where { id > 0l }.deleteAll()
        Application.where { id > 0l }.deleteAll()
        Node.where { id > 1l }.deleteAll()
    }
}
