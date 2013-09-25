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

/**
 * ProjectController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class ProjectController {
    def springSecurityService
    def projectService

    static expose = 'project'
    static scaffold = true
//	def index = { }
    static allowedMethods = [run: "POST"]


    def run(){
        def project = Project.get(params.id)
        response.setContentType("application/json")
        if(project == null){
            response.status = Response.SC_NOT_FOUND
            render([project:[:], message:"Project with id $params.id not found"] as JSON)
        }

        if(springSecurityService.loggedIn && springSecurityService.authentication.authorities*.authority.contains("ROLE_ADMINS")){
            if(project.state == ProjectState.RUNNING){
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
}
