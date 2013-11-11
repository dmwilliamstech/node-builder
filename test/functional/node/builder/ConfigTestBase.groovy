package node.builder

import node.builder.metrics.MetricsTestHelper

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
class ConfigTestBase extends NodeBuilderFunctionalTestBase{

    void shouldDisplayConfigurationLinks(){
        login()
        $('#dropdownAdmin').click()
        $('#linkAdminConfig').click()
        waitFor(10, 1){
            assert !$('a#configSubscriptionLevelsLink').empty
            assert !$('a#configOrganizationsLink').empty
        }
    }


}
