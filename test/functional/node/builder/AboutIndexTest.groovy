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

import org.junit.Before

class AboutIndexTest extends NodeBuilderFunctionalTestBase {
    def version
    def reference
    def dependencies

    @Before
    void setup(){
        def string = new File('application.properties').text
        def matcher = string =~ /app.version=(.*)/
        version = matcher[0][1]

        reference = this.class.classLoader.getResourceAsStream("reference.txt").text.replaceAll(/\s/, '')
        dependencies = new XmlSlurper().parseText(this.class.classLoader.getResourceAsStream("dependency-report.xml").text)

        assert version != null
        assert reference != null
        assert dependencies != null
    }

    void testIndex(){
        login()

        go('about')

        assert title == 'About'

        waitFor(10,1) {
            assert $('#application-name').text() == Config.applicationName
            assert $('#application-version').text().contains(version)
            assert $('#application-reference').text().replaceAll(/Reference\s/, "") == reference

            assert $('#list-dependency-build') != null
            assert $('#list-dependency-provided') != null
        }
    }

    void login(){
        go('login/auth')
        assert title == "Login"

        j_username = "admin"
        j_password = "admin"
        $('input#submit').click()
    }
}
