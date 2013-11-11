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
 * SubscriptionVariableController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SubscriptionVariableController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [subscriptionVariableInstanceList: SubscriptionVariable.list(params), subscriptionVariableInstanceTotal: SubscriptionVariable.count()]
    }

    def create() {
        [subscriptionVariableInstance: new SubscriptionVariable(params)]
    }

    def save() {
        def subscriptionVariableInstance = new SubscriptionVariable(params)
        if (!subscriptionVariableInstance.save(flush: true)) {
            render(view: "create", model: [subscriptionVariableInstance: subscriptionVariableInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), subscriptionVariableInstance.id])
        redirect(action: "show", id: subscriptionVariableInstance.id)
    }

    def show() {
        def subscriptionVariableInstance = SubscriptionVariable.get(params.id)
        if (!subscriptionVariableInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), params.id])
            redirect(action: "list")
            return
        }

        [subscriptionVariableInstance: subscriptionVariableInstance]
    }

    def edit() {
        def subscriptionVariableInstance = SubscriptionVariable.get(params.id)
        if (!subscriptionVariableInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), params.id])
            redirect(action: "list")
            return
        }

        [subscriptionVariableInstance: subscriptionVariableInstance]
    }

    def update() {
        def subscriptionVariableInstance = SubscriptionVariable.get(params.id)
        if (!subscriptionVariableInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (subscriptionVariableInstance.version > version) {
                subscriptionVariableInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable')] as Object[],
                          "Another user has updated this SubscriptionVariable while you were editing")
                render(view: "edit", model: [subscriptionVariableInstance: subscriptionVariableInstance])
                return
            }
        }

        subscriptionVariableInstance.properties = params

        if (!subscriptionVariableInstance.save(flush: true)) {
            render(view: "edit", model: [subscriptionVariableInstance: subscriptionVariableInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), subscriptionVariableInstance.id])
        redirect(action: "show", id: subscriptionVariableInstance.id)
    }

    def delete() {
        def subscriptionVariableInstance = SubscriptionVariable.get(params.id)
        if (!subscriptionVariableInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), params.id])
            redirect(action: "list")
            return
        }

        try {
            subscriptionVariableInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
