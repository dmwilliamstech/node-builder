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

import com.mongodb.AggregationOutput
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import node.builder.metrics.Metric
import node.builder.metrics.Metrics

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * MetricService
 * A service class encapsulates the core business logic of a Grails application
 */
class MetricService {

    static transactional = true

    def metricsForProject(projectName) {
        DBCollection collection = Metric.metricCollection()
        def metrics = [:]
        //match by project name
        BasicDBObject regexQuery = new BasicDBObject()
        regexQuery.put('$match', new BasicDBObject('eventId',
                new BasicDBObject('$regex', "$projectName.*".toString())
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

            def workflows = results
            metrics.workflows = workflows.results()
            metrics.averageDuration = averageDuration
        }
        return metrics
    }


}
