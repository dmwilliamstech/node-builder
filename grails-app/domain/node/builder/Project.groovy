package node.builder

/**
 * Project
 * A domain class describes the data object and it's mapping to the database
 */
class Project {
    static expose = 'project'

    /* Default (injected) attributes of GORM */
	Long	id

    String name
    String description
    String bpmn
    String location
    ProjectType projectType
    Boolean active
    String processDefinitionKey

    /* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated


    static mapping = {
    }

    static constraints = {
        bpmn maxSize: 16000
        name blank: false
        name unique: true
        location validator: {value , object ->
            if(!value.matches(/((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\+\\u0024,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\\u0024,\w]+@)[A-Za-z0-9.-]+)((?:[\\/\:][\+~%\\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[.\!\\/\\w]*))?)/)){
                return 'url'
            }
        }
    }

    /*
     * Methods of the Domain Class
     */
	@Override	// Override toString for a nicer / more descriptive UI
	public String toString() {
		return "${name}";
	}
}
