package node.builder

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Manifest
 * A domain class describes the manifest object and it's mapping to the manifestbase
 */
class Manifest {

	/* Default (injected) attributes of GORM */
	Long	id
	Long	version
	
	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

    JSONObject manifest //not persisted
    String manifestAsJSON //updated and synched with map


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
        manifestAsJSON( maxSize:20000)
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