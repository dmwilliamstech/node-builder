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
import node.builder.SubscriptionLevel
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(SubscriptionLevel)
class SubscriptionLevelTests {

    @Test
    void shouldCreateASubscriptionLevel(){
        def subscriptionLevel = new SubscriptionLevel()
        subscriptionLevel.name = "Level1"
        subscriptionLevel.description = "The first level"
        subscriptionLevel.count = 1

        subscriptionLevel.save()
        assert subscriptionLevel.validate() == true
    }


    @Test
    void shouldAllowPrettyBigDescription(){
        def subscriptionLevel = new SubscriptionLevel()
        subscriptionLevel.name = "Level1"
        subscriptionLevel.description = ".".multiply(10000)
        subscriptionLevel.count = 1

        subscriptionLevel.save()
        assert subscriptionLevel.validate() == true
    }

    @Test
    void shouldOnlyAllowNameOnce(){
        def subscriptionLevel = new SubscriptionLevel()
        subscriptionLevel.name = "Level1"
        subscriptionLevel.description = "Some description"
        subscriptionLevel.count = 1

        subscriptionLevel.save(flush: true)
        assert subscriptionLevel.validate() == true

        def subscriptionLevel2 = new SubscriptionLevel()
        subscriptionLevel2.name = "Level1"
        subscriptionLevel2.description = "Some description"
        subscriptionLevel2.count = 1

        subscriptionLevel2.save()
        assert subscriptionLevel2.validate() == false

        subscriptionLevel2.name = "Level2"
        subscriptionLevel2.save()
        assert subscriptionLevel2.validate() == true
    }
}
