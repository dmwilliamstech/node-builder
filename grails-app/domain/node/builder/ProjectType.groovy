package node.builder

/**
 * ProjectType
 * A domain class describes the data object and it's mapping to the database
 */
class ProjectType {

    /* Default (injected) attributes of GORM */
	Long	id

    String name

    /* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated


    static mapping = {
    }

    static constraints = {
    }

    /*
     * Methods of the Domain Class
     */
	@Override	// Override toString for a nicer / more descriptive UI
	public String toString() {
		return "${name}";
	}
}
