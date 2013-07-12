package node.builder

import grails.converters.JSON
import net.sf.json.JSONNull

/**
 * Instance
 * A domain class describes the data object and it's mapping to the database
 */
class Instance {
    static expose = 'instance'

	/* Default (injected) attributes of GORM */
	Long	id


    String name
    String status
    String hostId
    String privateIP
    String keyName
    String instanceId
    String userId
    String tenantId
    Integer progress
    String configDrive
    String metadata
    Manifest manifest
    Image image
    Flavor flavor

	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

//	static belongsTo	= [image: Image]	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static hasOne		= [manifest: Manifest]	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static mappedBy		= []	// specifies which property should be used in a mapping 
	
    static mapping = {
    }
    
	static constraints = {
        name(unique: false)
        instanceId(unique: true)
        privateIP(nullable: true)
        metadata(nullable: true)
        manifest(nullable: true)
        image(nullable: true)
        keyName(nullable: true)
        progress(nullable: true)
        configDrive(nullable: true)
        flavor(nullable: true)
    }
	
	/*
	 * Methods of the Domain Class
	 */
	@Override	// Override toString for a nicer / more descriptive UI
	public String toString() {
		return "${name}";
	}
}
