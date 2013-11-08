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
import org.apache.catalina.connector.Response
import org.apache.commons.io.FilenameUtils
import org.springframework.dao.DataIntegrityViolationException

/**
 * WorkflowController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class WorkflowController {
    def springSecurityService
    def workflowService
    def metricService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", run: "POST", completeTask: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [workflowInstanceList: workflowService.findAllByOrganizations(new TreeSet<String>(springSecurityService.principal.organizations), params), workflowInstanceTotal: Workflow.count()]
    }

    def create() {
        [workflowInstance: new Workflow(params)]
    }

    def save() {
        def workflowInstance = workflowService.createWithoutSaving(params, springSecurityService.principal.organizations)
        if (!workflowInstance.save(flush: true)) {
            render(view: "create", model: [workflowInstance: workflowInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'workflow.label', default: 'Workflow'), workflowInstance.id])
        redirect(action: "show", id: workflowInstance.id)
    }

    def show() {
        def workflowInstance = workflowService.getByOrganizations(params.id, new TreeSet<String>(springSecurityService.principal.organizations))
        if (!workflowInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "list")
            return
        }

        [workflowInstance: workflowInstance]
    }

    def edit() {
        def workflowInstance = workflowService.getByOrganizations(params.id, new TreeSet<String>(springSecurityService.principal.organizations))
        if (!workflowInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "list")
            return
        }

        [workflowInstance: workflowInstance]
    }

    def update() {
        def workflowInstance = Workflow.get(params.id)
        if (!workflowInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (workflowInstance.version > version) {
                workflowInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'workflow.label', default: 'Workflow')] as Object[],
                          "Another user has updated this Workflow while you were editing")
                render(view: "edit", model: [workflowInstance: workflowInstance])
                return
            }
        }

        if(!params.tags.toString().empty){
            workflowInstance = workflowService.addWorkflowTagsById(params.tags.split(','), workflowInstance)
            params.remove('tags')
        }

        workflowInstance.properties = params

        if (!workflowInstance.save(flush: true)) {
            render(view: "edit", model: [workflowInstance: workflowInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'workflow.label', default: 'Workflow'), workflowInstance.id])
        redirect(action: "show", id: workflowInstance.id)
    }

    def delete() {
        def workflowInstance = Workflow.get(params.id)
        if (!workflowInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "list")
            return
        }

        try {
            workflowInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def run(){
        def workflow = Workflow.get(params.id)
        response.setContentType("application/json")
        if(workflow == null){
            response.status = Response.SC_NOT_FOUND
            render([workflow:[:], message:"Workflow with id $params.id not found"] as JSON)
        }

        if(springSecurityService.loggedIn && springSecurityService.authentication.authorities*.authority.contains("ROLE_ADMINS")){
            if(workflow.state == WorkflowState.RUNNING || workflow.state == WorkflowState.WAITING){
                response.status = Response.SC_NOT_MODIFIED
                render([workflow:workflow, message:"Workflow with id $params.id already running"] as JSON)
                return true
            }
            def responseData = workflowService.run(workflow)
            response.status = workflow.state == WorkflowState.ERROR ? Response.SC_INTERNAL_SERVER_ERROR : Response.SC_OK
            render(responseData as JSON)
        }else{
            response.status = Response.SC_UNAUTHORIZED
            render([workflow:[:], message:"User ${springSecurityService.currentUser} is not authorized to run workflow with id $params.id"] as JSON)
        }
    }

    def completeTask(){
        def workflow = Workflow.get(params.id)
        response.setContentType("application/json")
        if(workflow == null){
            response.status = Response.SC_NOT_FOUND
            render([workflow:[:], message:"Workflow with id $params.id not found"] as JSON)
        }

        if(springSecurityService.loggedIn && springSecurityService.authentication.authorities*.authority.contains("ROLE_ADMINS")){
            if(workflow.state != WorkflowState.WAITING){
                response.status = Response.SC_NOT_MODIFIED
                render([workflow:workflow, message:"Workflow with id $params.id is not $WorkflowState.WAITING"] as JSON)
                return true
            }
            def responseData = workflowService.completeTask(workflow, params.decision)
            response.status = workflow.state == WorkflowState.ERROR ? Response.SC_INTERNAL_SERVER_ERROR : Response.SC_OK
            render(responseData as JSON)
        }else{
            response.status = Response.SC_UNAUTHORIZED
            render([workflow:[:], message:"User ${springSecurityService.currentUser} is not authorized to run workflow with id $params.id"] as JSON)
        }
    }

    def tags(){
        response.setContentType("application/json")
        if (request.method == 'POST'){
            if(springSecurityService.loggedIn && springSecurityService.authentication.authorities*.authority.contains("ROLE_ADMINS")){
                WorkflowTag.findOrCreateByName(request.JSON.name).save(flush: true)
            } else {
                response.status = Response.SC_UNAUTHORIZED
                render([workflow:[:], message:"User ${springSecurityService.currentUser} is not authorized to create workflow tags"] as JSON)
                return
            }
        }


        def tags = WorkflowTag.all//[]
//        WorkflowTag.executeQuery("select name as name, id as id from WorkflowTag ").each { it -> tags << [name: it[0], id: it[1]]}
        render(tags as JSON)
        return
    }

    def history(){
        def workflowInstance = workflowService.getByOrganizations(params.id, new TreeSet<String>(springSecurityService.principal.organizations))
        if (!workflowInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'workflow.label', default: 'Workflow'), params.id])
            redirect(action: "list")
            return
        }

        def metrics = metricService.metricsForWorkflow(workflowInstance.name)
        render(view: "history", model: [workflowInstance: workflowInstance, metrics: metrics])
    }

    def artifact(){
        def decodedId = java.net.URLDecoder.decode(params.id, "UTF-8")
        if(!decodedId.startsWith('http')){
            decodedId = 'file://' + decodedId
        }
        def url = new URL(decodedId)
        def filename = FilenameUtils.getBaseName( decodedId ) + FilenameUtils.getExtension( FilenameUtils.getExtension( decodedId ))
        try{
            def file
            response.setHeader("Content-Type", "text/plain")
            response.setHeader("Content-disposition", "attachment;filename=${filename}")

            file = url.openConnection()
            if(url.protocol.toLowerCase().matches('http')){
                def remoteAuth = "Basic " + "${springSecurityService.authentication.principal.username}:${springSecurityService.authentication.credentials}".getBytes().encodeBase64().toString()
                file.setRequestProperty("Authorization", remoteAuth);
            }
            response.outputStream << file.getInputStream()
            return
        }catch(e){
            log.error e.message
            if(e.getMessage().contains('401')){
                response.status = Response.SC_UNAUTHORIZED
                render([workflow:[:], message:"File ${filename} at ${url.path} not authorized"] as JSON)
                return

            }   else {
                response.status = Response.SC_NOT_FOUND
                render([workflow:[:], message:"File ${filename} at ${url.path} not found"] as JSON)
                return
            }
        }
    }
}