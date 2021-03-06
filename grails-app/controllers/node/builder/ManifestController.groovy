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
import node.builder.virt.OpenStackConnection
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils
import org.springframework.dao.DataIntegrityViolationException

/**
 * ManifestController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ManifestController {

    def manifestService
    def instanceService

    static allowedMethods = [create: "POST", update: "POST", delete: "DELETE", download: "POST", upload: "POST", undeploy: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        render(view: "list", model: [manifests:Manifest.list(params), manifestInstanceTotal: Manifest.count()])
    }

    def create() {
        def jsonObject = request.JSON

        def instance = new Manifest()
        instance.name = jsonObject.name
        instance.description = jsonObject.description
        instance.manifest = jsonObject
        instance.save()
        jsonObject.id = instance.id

        instance.manifest = jsonObject
        instance.save()
        if(instance.hasErrors())
            log.error instance.errors
        render(instance as JSON)
    }

    def show() {
        if(params.id == "new"){
            [manifestInstance: null, flavors: Flavor.all]
        }else{
            def manifestInstance = Manifest.get(params.id)
            if (!manifestInstance && params.id != "new") {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
                redirect(action: "list")
                return
            }

            [manifestInstance: manifestInstance, flavors: Flavor.all]
        }
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

        render(manifestService.manifestForJson(manifestInstance) as JSON)
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
            redirect(action: "list")
            return
        }
        def masterInstance = Master.first()
        render(view: "deploy", model: [manifest: manifestService.manifestForJson(manifestInstance), master: masterInstance, instances: Instance.all, images: Image.findAllByProgress(100)])
    }

    def download(){
        def manifestInstance = Manifest.get(params.id)
        if (!manifestInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(action: "list")
            return
        }

        response.setHeader("Content-Type", "text/text")// + params.filetype)
        response.setHeader("Content-disposition", "attachment;filename=${params.file}")
        response.outputStream << (manifestService.processTemplate(manifestInstance, "${GrailsResourceUtils.VIEWS_DIR_PATH}/templates/node.pp.gsp").bytes)
    }

    def upload(){
        def manifestInstance = Manifest.get(params.id)
        def masterInstance = Master.get(params.master)
        if (!manifestInstance || !masterInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            redirect(action: "list")
            return
        }

//        manifestService.deployTo(manifestInstance, masterInstance)
        instanceService.loadInstances(OpenStackConnection.connection)
        masterInstance = manifestService.deployToMasterAndProvision(manifestInstance, masterInstance)
//        masterInstance = manifestService.provision(manifestInstance)

        render(masterInstance as JSON)
    }

    def undeploy(){
        def manifestInstance = Manifest.get(params.id)
        def deploymentInstance = Deployment.get(params.deploymentId)
        def masterInstance = Master.get(1)
        if (!manifestInstance || !deploymentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manifest.label', default: 'Manifest'), params.id])
            render( [error : [message : 'Unknown ' + (manifestInstance? "deployment with id ${params.deploymentId}" : "manifest with id ${params.id}")]] as JSON )
            return
        }
        def result = manifestService.deprovisionAndUndeployFromMaster(manifestInstance, masterInstance, deploymentInstance)
        render(result as JSON)
    }

    def graph(){
        response.setHeader("Content-Type", "application/json")// + params.filetype)
        render((manifestService.generateGraph(params.id) as JSON))
    }
}
