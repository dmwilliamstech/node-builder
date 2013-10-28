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

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBObject
import groovy.time.TimeCategory
import node.builder.metrics.Metric
import node.builder.metrics.Metrics

/**
 * MetricService
 * A service class encapsulates the core business logic of a Grails application
 */
class MetricService {

    def metricsForProject(projectName) {
        DBCollection collection = Metric.metricCollection()
        def metrics = [:]
        //match by project name
        BasicDBObject regexQuery = new BasicDBObject()
        regexQuery.put('$match', new BasicDBObject('eventId',
                new BasicDBObject('$regex', "^$projectName\\-.*".toString())
                        .append('$options', 'i')))

        //only get the entries that have a duration
        BasicDBObject neQuery = new BasicDBObject()
        neQuery.put('$match', new BasicDBObject('duration',
                new BasicDBObject('$ne', -1)))

        //group to the average by group name
        DBObject groupFields = new BasicDBObject( '_id', '$group');
        groupFields.put('averageDuration', new BasicDBObject( '$avg', '$duration'));
        DBObject groupGroup = new BasicDBObject('$group', groupFields);

        //sort by dateCreated
        BasicDBObject sortOrder = new BasicDBObject()
        sortOrder.put('$sort', new BasicDBObject('lastUpdated',
                -1))

        BasicDBObject limit = new BasicDBObject()
        limit.put('$limit', 100)

        def results = collection.aggregate(neQuery, regexQuery, sortOrder, limit)
        def averages = collection.aggregate(regexQuery, neQuery, groupGroup)
        if(!results.collect().empty && averages.results().iterator().hasNext()){
            def average = averages.results()?.first()
            def millis = Double.valueOf(average.averageDuration)

            def averageDuration = Metrics.timeStringFromMilliseconds(millis)

            metrics.workflows = results.results()
            metrics.averageDuration = averageDuration
        }
        return metrics
    }

    def metricsForApplication() {
        def metrics = [:]
        metrics.upTime = upTime()

        return metrics
    }

    def upTime() {
        DBCollection collection = Metric.metricCollection()

        //match by group name
        BasicDBObject regexQuery = new BasicDBObject()
        regexQuery.put('$match', new BasicDBObject('group',
                new BasicDBObject('$regex', "up\\_time".toString())
                        .append('$options', 'i')))

        //sort by dateCreated
        BasicDBObject sortOrder = new BasicDBObject()
        sortOrder.put('$sort', new BasicDBObject('lastUpdated',
                -1))

        BasicDBObject limit = new BasicDBObject()
        limit.put('$limit', 1)

        def results = collection.aggregate(regexQuery, sortOrder, limit)
        use(TimeCategory) {
            return new Date() - results.results().first().lastUpdated
        }
    }
}
