package node.builder.metrics

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.MongoClient
import node.builder.Config
import org.bson.types.ObjectId

/**
 * Metric
 * A domain class describes the data object and it's mapping to the database
 */
class Metric {
    //use the mongo driver for this domain
    static DB db
    static def PROPERTY_LIST = ['clazz','thread','message','group','event', 'parentId','groupId','eventId', 'eventData','dateCreated','lastUpdated']
    static {
        MongoClient mongoClient = new MongoClient( Config.globalConfig.get("mongo.hostname") , Config.globalConfig.get("mongo.port"))
        db = mongoClient.getDB(Config.globalConfig.get("mongo.databaseName"))
    }

    ObjectId  id
    String  clazz
    String  thread
    String  message
    String  group
    String  event
    String  groupId
    String  eventId
    String  parentId
    BasicDBObject eventData

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
        id = object.get("_id")
    }
}
