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
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(InstanceService)
@Mock([Instance,Image,Flavor])
@TestMixin(ControllerUnitTestMixin)
class InstanceServiceTests {

    void testGetInstances() {
        InstanceService service = new InstanceService()
        service.loadInstances(OpenStackTestConnection.createConnection("resources/servers.json"))
        assert Instance.count == 2
    }

    void testUpdateInstances() {
        InstanceService service = new InstanceService()
        service.loadInstances(OpenStackTestConnection.createConnection("resources/servers.json"))
        assert Instance.count == 2

        service.loadInstances(OpenStackTestConnection.createConnection("resources/empty_array.json"))
        assert Instance.count == 0
    }
}
