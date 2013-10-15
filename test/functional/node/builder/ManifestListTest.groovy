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
import groovy.json.JsonSlurper
import org.junit.After
import org.junit.Before
import org.springframework.core.io.ClassPathResource

class ManifestListTest extends GebReportingTest {

    @Before
    void setup(){
        def manifest = new Manifest()
        manifest.name = "Test"
        manifest.description = "Test"
        manifest.manifest = (new JsonSlurper()).parseText((new ClassPathResource("resources/manifest.json").getFile().text))
        manifest.save(failOnError: true)

        assert Manifest.all.size() == 1
    }

    void testManifestList(){
        login()
        assert title == "Manifest List"
        assert $('h4').first().text() == "Test"
        assert $('.icon-pencil') != null
        assert $('.icon-upload') != null
        assert $('.icon-remove-sign') != null
        assert $('.icon-info-sign') != null
    }

    void testManifestNew(){
        login()
        assert title == "Manifest List"
        $('i.icon-plus-sign').click()
        assert $('h4').first().text() == "Manifest Name"
    }

    void login(){
        go('login/auth')
        assert title == "Login"

        j_username = "admin"
        j_password = "admin"
        $('input#submit').click()

        $('.dropdown-toggle').click()
        $('a[href$="manifest"]').click()
    }
    @After
    void teardown(){
        Manifest.where { id > 0l }.deleteAll()
        assert Manifest.all.size() == 0
        ApplicationConfiguration.where { id > 0l }.deleteAll()
        NodeConfiguration.where { id > 0l }.deleteAll()
        Application.where { id > 0l }.deleteAll()
        Node.where { id > 1l }.deleteAll()
    }
}
