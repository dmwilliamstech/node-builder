package node.builder

/**
 * Image
 * A domain class describes the data object and it's mapping to the database
 */
class Image {
    static expose = 'image'
	/* Default (injected) attributes of GORM */
	Long id
	Long	version

    String name
    String imageId
    Integer progress
    Integer minDisk
    Integer minRam
    String status

	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated
	
//	static belongsTo	= []	// tells GORM to cascade commands: e.g., delete this object if the "parent" is deleted.
//	static hasOne		= []	// tells GORM to associate another domain object as an owner in a 1-1 mapping
//	static hasMany		= []	// tells GORM to associate other domain objects for a 1-n or n-m mapping
//	static mappedBy		= []	// specifies which property should be used in a mapping 
	
    static mapping = {
    }
    
	static constraints = {
        name(unique: true)
    }
	
	/*
	 * Methods of the Domain Class
	 */
	@Override	// Override toString for a nicer / more descriptive UI
	public String toString() {
		return "${name}";
	}
}


/*
image: [
            status: ACTIVE,
            updated: 2013-06-12T20: 34: 11Z,
            links: [
                [
                    href: http: //107.2.16.122: 8774/v2/2ba2d60c5e8d4d1b86549d988131fe48/images/3012812c-c178-408b-9b54-b292525834c3,
                    rel: self
                ],
                [
                    href: http: //107.2.16.122: 8774/2ba2d60c5e8d4d1b86549d988131fe48/images/3012812c-c178-408b-9b54-b292525834c3,
                    rel: bookmark
                ],
                [
                    href: http: //10.0.1.49: 9292/2ba2d60c5e8d4d1b86549d988131fe48/images/3012812c-c178-408b-9b54-b292525834c3,
                    type: application/vnd.openstack.image,
                    rel: alternate
                ]
            ],
            id: 3012812c-c178-408b-9b54-b292525834c3,
            name: base+avahi,
            created: 2013-06-12T20: 33: 09Z,
            minDisk: 0,
            server: [
                id: 1842eedf-d197-4120-80fd-64e7560cb8f1,
                links: [
                    [
                        href: http: //107.2.16.122: 8774/v2/2ba2d60c5e8d4d1b86549d988131fe48/servers/1842eedf-d197-4120-80fd-64e7560cb8f1,
                        rel: self
                    ],
                    [
                        href: http: //107.2.16.122: 8774/2ba2d60c5e8d4d1b86549d988131fe48/servers/1842eedf-d197-4120-80fd-64e7560cb8f1,
                        rel: bookmark
                    ]
                ]
            ],
            progress: 100,
            minRam: 0,
            metadata: [
                instance_uuid: 1842eedf-d197-4120-80fd-64e7560cb8f1,
                image_location: snapshot,
                image_state: available,
                user_id: ce51d53b37264954aa3473c72c0bf8b7,
                image_type: snapshot,
                base_image_ref: 941b9236-611a-41a0-9e5c-24a2b1a8c0ae,
                owner_id: 2ba2d60c5e8d4d1b86549d988131fe48
            ]
        ]
*/