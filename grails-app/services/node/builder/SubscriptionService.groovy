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

import node.builder.user.CustomUserDetails
import org.springframework.security.core.GrantedAuthority

/**
 * SubscriptionService
 * A service class encapsulates the core business logic of a Grails application
 */
class SubscriptionService {

    static transactional = true

    def subscriptionsForUser(CustomUserDetails user, params){
        def subscriptions = []
        if(user.authorities.find{GrantedAuthority auth -> auth.authority.contains('NBADMINS')}){
            subscriptions.addAll(Subscription.list(params))
        }else{
            subscriptions = Subscription.findAllByOrganizationInList(user.organizations)
        }

        return subscriptions
    }

    def saveWithVariables(Subscription subscriptionInstance, workflowTagVariables){
        subscriptionInstance.variables?.clear()
        if(!subscriptionInstance.save(flush: true))
            return false

        workflowTagVariables.each { name, value ->
            def subscriptionVariable = new SubscriptionVariable()

            subscriptionVariable.subscription = subscriptionInstance
            subscriptionVariable.name = name
            subscriptionVariable.value = value
            subscriptionVariable.save(flush: true)

            subscriptionInstance.addToVariables(subscriptionVariable)
        }
        return true
    }
}
