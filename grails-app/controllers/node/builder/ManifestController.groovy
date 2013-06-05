package node.builder

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

/**
 * ManifestController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ManifestController {

    static allowedMethods = [create: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        render([manifests:Manifest.list(params), manifestInstanceTotal: Manifest.count()] as JSON)
    }


    def create() {
        def jsonObject = request.JSON

        def instance = new Manifest()
        instance.manifest = jsonObject
        instance.save()
        render(instance as JSON)
    }

    def show() {
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            response.sendError(404)
            return
        }

        render(manifestInstance as JSON)
    }

    def update() {
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (manifestInstance.version > version) {
                manifestInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'manifest.label', default: 'Manifest')] as Object[],
                          "Another user has updated this Manifest while you were editing")
                render(view: "edit", model: [manifestInstance: manifestInstance])
                return
            }
        }

        manifestInstance.properties = params

        if (!manifestInstance.save(flush: true)) {
            render(view: "edit", model: [manifestInstance: manifestInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'manifest.label', default: 'Manifest'), manifestInstance.id])
        redirect(action: "show", id: manifestInstance.id)
    }

    def delete() {
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(action: "list")
            return
        }

        try {
            manifestInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
