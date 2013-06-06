package node.builder

import grails.converters.JSON
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils
import org.springframework.dao.DataIntegrityViolationException

/**
 * ManifestController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ManifestController {

    def groovyPagesTemplateEngine

    static allowedMethods = [create: "POST", update: "POST", delete: "DELETE", download: "POST"]

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
        jsonObject.id = instance.id

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
        def jsonObject = request.JSON
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(controller: 'home', action: "index")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (manifestInstance.version > version) {
                manifestInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'manifest.label', default: 'Manifest')] as Object[],
                          "Another user has updated this Manifest while you were editing")
                redirect(controller: 'home', action: "index")
                return
            }
        }

        manifestInstance.manifest = jsonObject

        if (!manifestInstance.save(flush: true)) {
            manifestInstance.errors.rejectValue("manifest", "Invalid Manifest Document",
                    [message(code: 'manifest.label', default: 'Manifest')] as Object[],
                    "")
            redirect(view: "configure", model: [manifest: manifestInstance.manifest])
            return
        }

        render(manifestInstance as JSON)
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

    def configure(){
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(controller: 'home', action: "index")
            return
        }
        render(view: "configure", model: [manifest: manifestInstance])
    }

    def deploy(){
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(controller: 'home', action: "index")
            return
        }
        render(view: "deploy", model: [manifest: manifestInstance])
    }

    def download(){
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(controller: 'home', action: "index")
            return
        }

        def templateText = new File("${GrailsResourceUtils.VIEWS_DIR_PATH}/templates/site.pp.gsp").text

        def output = new StringWriter()
        groovyPagesTemplateEngine.createTemplate(templateText, 'site.pp').make([manifest: manifestInstance.manifest, items: ['Grails','Groovy']]).writeTo(output)

        response.setHeader("Content-Type", "text/text")// + params.filetype)
        response.setHeader("Content-disposition", "attachment;filename=${params.file}")
        response.outputStream << (output.getBuffer().toString().bytes)
    }
}
