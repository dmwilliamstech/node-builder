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

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

/**
 * InstanceController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class InstanceController {
    InstanceService instanceService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", launch: "POST"]

    def index() {
        instanceService.loadInstances(OpenStackConnection.getConnection())
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [instanceInstanceList: Instance.list(params), instanceInstanceTotal: Instance.count()]
    }

    def create() {
        [instanceInstance: new Instance(params)]
    }

    def save() {
        def instanceInstance = new Instance(params)
        if (!instanceInstance.save(flush: true)) {
            render(view: "create", model: [instanceInstance: instanceInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'instance.label', default: 'Instance'), instanceInstance.id])
        redirect(action: "show", id: instanceInstance.id)
    }

    def show() {
        def instanceInstance = Instance.get(params.id)
        if (!instanceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])
            redirect(action: "list")
            return
        }

        [instanceInstance: instanceInstance]
    }

    def edit() {
        def instanceInstance = Instance.get(params.id)
        if (!instanceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])
            redirect(action: "list")
            return
        }

        [instanceInstance: instanceInstance]
    }

    def update() {
        def instanceInstance = Instance.get(params.id)
        if (!instanceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (instanceInstance.version > version) {
                instanceInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'instance.label', default: 'Instance')] as Object[],
                          "Another user has updated this Instance while you were editing")
                render(view: "edit", model: [instanceInstance: instanceInstance])
                return
            }
        }

        instanceInstance.properties = params

        if (!instanceInstance.save(flush: true)) {
            render(view: "edit", model: [instanceInstance: instanceInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'instance.label', default: 'Instance'), instanceInstance.id])
        redirect(action: "show", id: instanceInstance.id)
    }

    def delete() {
        def instanceInstance = Instance.get(params.id)
        if (!instanceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])
            redirect(action: "list")
            return
        }

        try {
            instanceInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'instance.label', default: 'Instance'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def launch() {
        def jsonObject = request.JSON

        try{
            def connection = new OpenStackConnection("107.2.16.122", "admin", "stack", "2ba2d60c5e8d4d1b86549d988131fe48")
            def response = connection.launch("1", jsonObject.imageId, "opendx_demo", jsonObject.instanceName)
            render (response as JSON)
        }catch(Exception e){
            log.error "Failed to launch instance ${jsonObject.instanceName} on OpenStack - ${e}"
        }
    }
}
