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
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Manifest
 * A domain class describes the manifest object and it's mapping to the manifestbase
 */
class Manifest {
    static expose = 'manifest'
	/* Default (injected) attributes of GORM */
	Long	id
//	Long	version
	
	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

    String name
    String description
    JSONObject manifest //not persisted
    String manifestAsJSON //updated and synched with map


    static hasMany		= [deployments: Deployment]	// tells GORM to associate other domain objects for a 1-n or n-m mapping

    public Manifest(String json){
        manifestAsJSON = json
        manifest = JSON.parse(json)
    }

    def afterLoad() {
        manifest=JSON.parse(manifestAsJSON)
    }


    def beforeValidate() {
        manifestAsJSON= manifest as JSON
    }

    static transients = ['manifest']

    static constraints = {
        manifestAsJSON(maxSize: 200000)
        description(nullable: true)
    }

	/*
	 * Methods of the Domain Class
	 */
	@Override	// Override toString for a nicer / more descriptive UI
	public String toString() {
		return "${manifestAsJSON}";
	}



    def setManifest(JSONObject map){
        manifest = map
        manifestAsJSON = map as JSON
    }

}
