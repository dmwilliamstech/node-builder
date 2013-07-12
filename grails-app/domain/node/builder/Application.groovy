package node.builder

/**
 * Application
 * A domain class describes the data object and it's mapping to the database
 */
class Application {
    static expose = 'application'

	/* Default (injected) attributes of GORM */
	Long	id

    String  name
    String  description
    String  flavorId
	
	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

    static belongsTo	= [node: Node]	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
	static hasMany		= [configurations: ApplicationConfiguration]	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static mappedBy		= []	// specifies which property should be used in a mapping 
	
    static mapping = {
    }
    
	static constraints = {
        name(unique: true)
        flavorId(nullable: true)
    }
	
	/*
	 * Methods of the Domain Class
	 */
//	@Override	// Override toString for a nicer / more descriptive UI 
//	public String toString() {
//		return "${name}";
//	}
}
