package node.builder

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

/**
 * InstanceController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class InstanceController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", launch: "POST"]

    def index() {
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
