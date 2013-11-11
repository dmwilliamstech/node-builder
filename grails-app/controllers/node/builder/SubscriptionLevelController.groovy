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
 * SubscriptionLevelController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class SubscriptionLevelController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [subscriptionLevelInstanceList: SubscriptionLevel.list(params), subscriptionLevelInstanceTotal: SubscriptionLevel.count()]
    }

    def create() {
        [subscriptionLevelInstance: new SubscriptionLevel(params)]
    }

    def save() {
        def subscriptionLevelInstance = new SubscriptionLevel(params)
        if (!subscriptionLevelInstance.save(flush: true)) {
            render(view: "create", model: [subscriptionLevelInstance: subscriptionLevelInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), subscriptionLevelInstance.id])
        redirect(action: "show", id: subscriptionLevelInstance.id)
    }

    def show() {
        def subscriptionLevelInstance = SubscriptionLevel.get(params.id)
        if (!subscriptionLevelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), params.id])
            redirect(action: "list")
            return
        }

        [subscriptionLevelInstance: subscriptionLevelInstance]
    }

    def edit() {
        def subscriptionLevelInstance = SubscriptionLevel.get(params.id)
        if (!subscriptionLevelInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), params.id])
            redirect(action: "list")
            return
        }

        [subscriptionLevelInstance: subscriptionLevelInstance]
    }

    def update() {
        def subscriptionLevelInstance = SubscriptionLevel.get(params.id)
        if (!subscriptionLevelInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (subscriptionLevelInstance.version > version) {
                subscriptionLevelInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel')] as Object[],
                          "Another user has updated this SubscriptionLevel while you were editing")
                render(view: "edit", model: [subscriptionLevelInstance: subscriptionLevelInstance])
                return
            }
        }

        subscriptionLevelInstance.properties = params

        if (!subscriptionLevelInstance.save(flush: true)) {
            render(view: "edit", model: [subscriptionLevelInstance: subscriptionLevelInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), subscriptionLevelInstance.id])
        redirect(action: "show", id: subscriptionLevelInstance.id)
    }

    def delete() {
        def subscriptionLevelInstance = SubscriptionLevel.get(params.id)
        if (!subscriptionLevelInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), params.id])
            redirect(action: "list")
            return
        }

        try {
            subscriptionLevelInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
