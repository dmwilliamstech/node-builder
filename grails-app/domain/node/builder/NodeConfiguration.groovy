package node.builder

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

/**
 * Configuration
 * A domain class describes the data object and it's mapping to the database
 */
class NodeConfiguration {
    static expose = 'node-configuration'
	/* Default (injected) attributes of GORM */
	Long	id
//	Long	version

    String name
    Object value
    String valueAsJson
    String description

	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

    def afterLoad() {
        value=JSON.parse(valueAsJson)
    }

    def beforeValidate() {
        valueAsJson= (new JSONObject([json:value])) as JSON
    }

    static transients = ['value']

	static belongsTo	= [node: Node]	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static mappedBy		= []	// specifies which property should be used in a mapping 
	
    static mapping = {
    }
    
	static constraints = {
        description(nullable: true)
    }

    /*
     * Methods of the Domain Class
     */
    @Override	// Override toString for a nicer / more descriptive UI
    public String toString() {
        return "${name} - value ${value}";
    }
}


