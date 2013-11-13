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

import org.springframework.dao.DataIntegrityViolationException

/**
 * SubscriptionController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SubscriptionController {
    def subscriptionService
    def springSecurityService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        def subscriptions = subscriptionService.subscriptionsForUser(springSecurityService.principal, params)
        [subscriptionInstanceList: subscriptions, subscriptionInstanceTotal: subscriptions.size(), organizations: springSecurityService.principal.organizations]
    }

    def create() {
        [subscriptionInstance: new Subscription(params),
                organizations: springSecurityService.principal.organizations,
                availableVariables: WorkflowTag.allAvailableWorkflowVariables()
        ]
    }

    def save() {
        def subscriptionInstance = new Subscription(params)
        if (!subscriptionService.saveWithVariables(subscriptionInstance, params.workflowTagVariables)) {
            render(view: "create", model: [subscriptionInstance: subscriptionInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'subscription.label', default: 'Subscription'), subscriptionInstance.id])
        redirect(action: "list")
    }

    def show() {
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect(action: "list")
            return
        }

        [subscriptionInstance: subscriptionInstance, organizations: springSecurityService.principal.organizations]
    }

    def edit() {
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect(action: "list")
            return
        }

        [subscriptionInstance: subscriptionInstance, organizations: springSecurityService.principal.organizations,
                availableVariables: WorkflowTag.allAvailableWorkflowVariables()]
    }

    def update() {
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (subscriptionInstance.version > version) {
                subscriptionInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'subscription.label', default: 'Subscription')] as Object[],
                          "Another user has updated this Subscription while you were editing")
                render(view: "edit", model: [subscriptionInstance: subscriptionInstance])
                return
            }
        }

        subscriptionInstance.properties = params

        if (!subscriptionService.saveWithVariables(subscriptionInstance, params.workflowTagVariables)) {
            render(view: "edit", model: [subscriptionInstance: subscriptionInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'subscription.label', default: 'Subscription'), subscriptionInstance.id])
        redirect(action: "list")
    }

    def delete() {
        def subscriptionInstance = Subscription.get(params.id)
        if (!subscriptionInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect(action: "list")
            return
        }

        try {
            subscriptionInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subscription.label', default: 'Subscription'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
