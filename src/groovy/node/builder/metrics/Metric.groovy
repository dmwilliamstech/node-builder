package node.builder.metrics

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.MongoClient
import node.builder.Config

/**
 * Metric
 * A domain class describes the data object and it's mapping to the database
 */
class Metric {
    //use the mongo driver for this domain
    static DB db
    static def PROPERTY_LIST = ['clazz','thread','message','group','event','dateCreated','lastUpdated']
    static {
        MongoClient mongoClient = new MongoClient( Config.globalConfig.get("mongo.hostname") , Config.globalConfig.get("mongo.port"))
        db = mongoClient.getDB(Config.globalConfig.get("mongo.databaseName"))
    }

	Long	id
    String  clazz
    String  thread
    String  message
    String  group
    String  event

	/* Automatic timestamping of GORM */
	Date	dateCreated
	Date	lastUpdated

    def save(){
        dateCreated = Date.newInstance()
        lastUpdated = Date.newInstance()
        BasicDBObject object = new BasicDBObject()
        PROPERTY_LIST.each(){propertyName ->
            object.append(propertyName, this.properties[propertyName])
        }

        db.getCollection("metric").insert(object)
    }
}
