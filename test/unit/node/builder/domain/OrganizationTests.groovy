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
import node.builder.Organization
import node.builder.SubscriptionLevel
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

@TestFor(Organization)
@Mock([SubscriptionLevel])
class OrganizationTests {

    @Test
    void shouldCreateANewOrganization(){
        def subscriptionLevel = new SubscriptionLevel(name: "Level99", description: "Some words", subscriptionCount: 99)
        subscriptionLevel.save()

        def organization = new Organization()
        organization.name = "Some Org"
        organization.description = "Some Org"
        organization.subscriptionLevel = subscriptionLevel

        assert organization.validate() == true
    }
}
