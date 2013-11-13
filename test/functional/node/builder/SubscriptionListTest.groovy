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

import org.junit.After
import org.junit.Before
import org.junit.Test

class SubscriptionListTest extends NodeBuilderFunctionalTestBase{

    @Before
    void setup(){

    }

    @Test
    void shouldDisplaySubscriptionList(){
        login()
        go('subscription')
        assert title == "Subscription List"

    }

    @After
    void teardown(){

        deleteEmptyRepo()
    }
}
