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
import org.springframework.dao.DataIntegrityViolationException

/**
 * ProjectController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ProjectController {
    def springSecurityService
    def projectService
    def metricService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", run: "POST", completeTask: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [projectInstanceList: projectService.findAllByOrganizations(new TreeSet<String>(springSecurityService.principal.organizations), params), projectInstanceTotal: Project.count()]
    }

    def create() {
        [projectInstance: new Project(params)]
    }

    def save() {
        def projectInstance = projectService.createWithoutSaving(params, springSecurityService.principal.organizations)
        if (!projectInstance.save(flush: true)) {
            render(view: "create", model: [projectInstance: projectInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'project.label', default: 'Project'), projectInstance.id])
        redirect(action: "show", id: projectInstance.id)
    }

    def show() {
        def projectInstance = projectService.getByOrganizations(params.id, new TreeSet<String>(springSecurityService.principal.organizations))
        if (!projectInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
            return
        }

        [projectInstance: projectInstance]
    }

    def edit() {
        def projectInstance = projectService.getByOrganizations(params.id, new TreeSet<String>(springSecurityService.principal.organizations))
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
            return
        }

        [projectInstance: projectInstance]
    }

    def update() {
        def projectInstance = Project.get(params.id)
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (projectInstance.version > version) {
                projectInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'project.label', default: 'Project')] as Object[],
                          "Another user has updated this Project while you were editing")
                render(view: "edit", model: [projectInstance: projectInstance])
                return
            }
        }

        projectInstance.properties = params

        if (!projectInstance.save(flush: true)) {
            render(view: "edit", model: [projectInstance: projectInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'project.label', default: 'Project'), projectInstance.id])
        redirect(action: "show", id: projectInstance.id)
    }

    def delete() {
        def projectInstance = Project.get(params.id)
        if (!projectInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
            return
        }

        try {
            projectInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def run(){
        def project = Project.get(params.id)
        response.setContentType("application/json")
        if(project == null){
            response.status = Response.SC_NOT_FOUND
            render([project:[:], message:"Project with id $params.id not found"] as JSON)
        }

        if(springSecurityService.loggedIn && springSecurityService.authentication.authorities*.authority.contains("ROLE_ADMINS")){
            if(project.state == ProjectState.RUNNING || project.state == ProjectState.WAITING){
                response.status = Response.SC_NOT_MODIFIED
                render([project:project, message:"Project with id $params.id already running"] as JSON)
                return true
            }
            def responseData = projectService.run(project)
            response.status = project.state == ProjectState.ERROR ? Response.SC_INTERNAL_SERVER_ERROR : Response.SC_OK
            render(responseData as JSON)
        }else{
            response.status = Response.SC_UNAUTHORIZED
            render([project:[:], message:"User ${springSecurityService.currentUser} is not authorized to run project with id $params.id"] as JSON)
        }
    }

    def completeTask(){
        def project = Project.get(params.id)
        response.setContentType("application/json")
        if(project == null){
            response.status = Response.SC_NOT_FOUND
            render([project:[:], message:"Project with id $params.id not found"] as JSON)
        }

        if(springSecurityService.loggedIn && springSecurityService.authentication.authorities*.authority.contains("ROLE_ADMINS")){
            if(project.state != ProjectState.WAITING){
                response.status = Response.SC_NOT_MODIFIED
                render([project:project, message:"Project with id $params.id is not $ProjectState.WAITING"] as JSON)
                return true
            }
            def responseData = projectService.completeTask(project, params.decision)
            response.status = project.state == ProjectState.ERROR ? Response.SC_INTERNAL_SERVER_ERROR : Response.SC_OK
            render(responseData as JSON)
        }else{
            response.status = Response.SC_UNAUTHORIZED
            render([project:[:], message:"User ${springSecurityService.currentUser} is not authorized to run project with id $params.id"] as JSON)
        }
    }

    def history(){
        def projectInstance = projectService.getByOrganizations(params.id, new TreeSet<String>(springSecurityService.principal.organizations))
        if (!projectInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
            redirect(action: "list")
            return
        }

        def metrics = metricService.metricsForProject(projectInstance.name)
        render(view: "history", model: [projectInstance: projectInstance, metrics: metrics])
    }

    def artifact(){
        def file = new File(java.net.URLDecoder.decode(params.id, "UTF-8"))
        if(!file.exists() || !file.file){
            response.status = Response.SC_NOT_FOUND
            render([project:[:], message:"File with id $params.id not found"] as JSON)
        }

        response.setHeader("Content-Type", "text/plain")
        response.setHeader("Content-disposition", "attachment;filename=${file.name}")
        response.outputStream << file.newInputStream()
    }
}