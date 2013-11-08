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
import node.builder.Subscription
import node.builder.SubscriptionLevel
import node.builder.WorkflowTag
import org.junit.Before
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Subscription)
@Mock([SubscriptionLevel, Organization, WorkflowTag])
class SubscriptionTests {

    SubscriptionLevel subscriptionLevel
    Organization organization
    WorkflowTag workflowTag

    @Before
    void setup(){
        subscriptionLevel = new SubscriptionLevel(name: "Level99", description: "Some words", count: 99)
        subscriptionLevel.save()

        organization = new Organization()
        organization.name = "Some Org"
        organization.subscriptionLevel = subscriptionLevel

        assert organization.validate() == true

        workflowTag = new WorkflowTag(name: "Workflows")
        workflowTag.save()

        assert workflowTag.validate() == true
    }


    @Test
    void shouldCreateNewSubscription(){
        def subscription = new Subscription()
        subscription.organization = organization
        subscription.workflowTag = workflowTag

        subscription.save()
        assert subscription.validate() == true
    }

    @Test
    void shouldCreateNewSubscriptionwithVariables(){
        def subscription = new Subscription()
        subscription.organization = organization
        subscription.workflowTag = workflowTag
        subscription.variables.key = 'value'

        subscription.save()
        assert subscription.validate() == true
        assert subscription.variables.key == 'value'
    }

    @Test
    void shouldAllowRelationshipTraversal(){
        def subscription = new Subscription()
        subscription.organization = organization
        subscription.workflowTag = workflowTag
        subscription.variables.key = 'value'
        subscription.save()
        assert subscription.validate() == true

        workflowTag.addToSubscriptions(subscription)

        subscriptionLevel.addToOrganizations(organization)
        organization.addToSubscriptions(subscription)

        assert workflowTag.subscriptions.first().variables.key == 'value'
        assert subscriptionLevel.organizations.first().subscriptions.first().variables.key == 'value'
    }
}
