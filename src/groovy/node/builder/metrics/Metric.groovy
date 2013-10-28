package node.builder.metrics

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.MongoClient
import grails.util.Environment
import node.builder.Config
import org.apache.commons.logging.impl.LogFactoryImpl
import org.bson.types.ObjectId

/**
 * Metric
 * A domain class describes the data object and it's mapping to the database
 */
class Metric {
    //use the mongo driver for this domain
    static DB db
    static Boolean connected = false
    static final def PROPERTY_LIST = ['clazz','thread','message','group','event', 'parentId','groupId','eventId', 'eventData','dateCreated','lastUpdated','duration']
    static final def METRIC_COLLECTION_NAME = 'metric_' + Environment.current.name.toLowerCase()
    static final def METRIC_ID_FIELD = '_id'
    static {
        if(Config.globalConfig.get("mongo.hostname") != null && Config.globalConfig.get("mongo.port") != null){
            try{
                MongoClient mongoClient = new MongoClient( Config.globalConfig.get("mongo.hostname") , Config.globalConfig.get("mongo.port"))
                db = mongoClient.getDB(Config.globalConfig.get("mongo.databaseName"))
                if(db){
                    println '| Connected to mongo for logging'
                    connected = true
                }
                if(Environment.current == Environment.TEST){
                    db.getCollection(METRIC_COLLECTION_NAME).drop()
                }
            }catch(e){
                LogFactoryImpl.getLog(this).warn("Unable to connect to mongod server")
            }
        }
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
    Double  duration

    def save(){
        if(connected){
            dateCreated = Date.newInstance()
            lastUpdated = Date.newInstance()
            BasicDBObject object = new BasicDBObject()
            PROPERTY_LIST.each(){propertyName ->
                object.append(propertyName, this.properties[propertyName])
            }

            db.getCollection(METRIC_COLLECTION_NAME).insert(object)
            id = object.get(METRIC_ID_FIELD)
        }
    }

    static def metricCollection(){
        return db.getCollection(METRIC_COLLECTION_NAME)
    }

    static def resetMetrics(){
        if(Environment.current == Environment.TEST){
            BasicDBObject query = new BasicDBObject()
            query.append('group', MetricGroups.TASK.title)
            db.getCollection(METRIC_COLLECTION_NAME).remove(query)

            query = new BasicDBObject()
            query.append('group', MetricGroups.WORKFLOW.title)
            db.getCollection(METRIC_COLLECTION_NAME).remove(query)
        } else {
            throw new Exception("Cannot reset metrics in ${Environment.current.name}")
        }
    }
}
