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

class UrlMappings {

	static mappings = {
		
		/* 
		 * Pages without controller 
		 */
//		"/"				(view:"/index")
		"/blog"			(view:"/siteinfo/blog")
		"/systeminfo"	(view:"/siteinfo/systeminfo")
		"/contact"		(view:"/siteinfo/contact")
		"/terms"		(view:"/siteinfo/terms")
		"/imprint"		(view:"/siteinfo/imprint")
		"/nextSteps"	(view:"/home/nextSteps")
		
		/* 
		 * Pages with controller
		 * WARN: No domain/controller should be named "api" or "mobile" or "web"!
		 */
        "/login/$action?"(controller: "login")
        "/logout/$action?"(controller: "logout")

        "/"	{
			controller	= 'workflow'
			action		= { 'index' }
            view		= { 'index' }
        }
//        "/manifest/manifest"	{
//            controller	= 'manifest'
//            action		= { 'manifest' }
//            view		= { 'new' }
//        }
        "/deploy/update/$id"	{
            controller	= 'manifest'
            action		= { 'update' }
            view		= { 'configure' }
        }
        "/configure/$id"	{
            controller	= 'manifest'
            action		= { 'configure' }
            view		= { 'configure' }
        }
        "/manifest/deploy/$id/upload/$master"	{
            controller	= 'manifest'
            action		= { 'upload' }
            view		= { 'deploy' }
        }
        "/manifest/undeploy/$id/deployment/$deploymentId"	{
            controller	= 'manifest'
            action		= { 'undeploy' }
            view		= { 'list' }
        }
        "/deploy/$id/download/$file"	{
            controller	= 'manifest'
            action		= { 'download' }
            view		= { 'deploy' }
        }
        "/deploy/$id"	{
            controller	= 'manifest'
            action		= { 'deploy' }
            view		= { 'deploy' }
        }
        "/api/v1/dependency-report" {
            controller = 'dependencyReport'
            action		= { 'index' }
        }
		"/$controller/$action?/$id?"{
			constraints {
				controller(matches:/^((?!(api|mobile|web)).*)$/)
		  	}
		}
		
		/* 
		 * System Pages without controller 
		 */
		"403"	(view:'/_errors/403')
		"404"	(view:'/_errors/404')
		"500"	(view:'/_errors/error')
		"503"	(view:'/_errors/503')
	}
}
