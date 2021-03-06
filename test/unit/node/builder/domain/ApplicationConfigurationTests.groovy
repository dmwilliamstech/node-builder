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

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import node.builder.Application
import node.builder.ApplicationConfiguration

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(ApplicationConfiguration)
@TestMixin(ControllerUnitTestMixin)
class ApplicationConfigurationTests {

    void testWithoutDescriptions() {
        def params = [name : "Config", value: "Value", application: new Application()]
        def applicationConfiguration= new ApplicationConfiguration(params)
        applicationConfiguration.save()

        assert applicationConfiguration.errors.errorCount == 0
        assert applicationConfiguration.description == null
    }

    void testDescriptions() {
        def params = [name : "Config", value: "Value", description: "Description", application: new Application()]
        def applicationConfiguration= new ApplicationConfiguration(params)
        applicationConfiguration.save()

        assert applicationConfiguration.errors.errorCount == 0
        assert applicationConfiguration.description == "Description"
    }
}
