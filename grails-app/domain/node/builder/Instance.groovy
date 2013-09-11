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
    Deployment deployment
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
        deployment(nullable: true)
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
