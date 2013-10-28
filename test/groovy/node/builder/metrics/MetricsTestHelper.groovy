package node.builder.metrics

import com.mongodb.BasicDBObject
import grails.util.Environment
import groovy.json.JsonSlurper
import org.springframework.core.io.ClassPathResource

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
class MetricsTestHelper {
    static {
        if(Environment.current == Environment.TEST){
            Metric.metricCollection().drop()
        }
    }


    static def resetMetrics(){
        if(Environment.current == Environment.TEST){
            BasicDBObject query = new BasicDBObject()
            query.append('group', MetricGroups.TASK.title)
            Metric.metricCollection().remove(query)

            query = new BasicDBObject()
            query.append('group', MetricGroups.WORKFLOW.title)
            Metric.metricCollection().remove(query)
        } else {
            throw new Exception("Cannot reset metrics in ${Environment.current.name}")
        }
    }

    static def seedTestData(){
        def json = new ClassPathResource("resources/metrics.json").getFile().text
        def jsonSlurper = new JsonSlurper()
        def documents = jsonSlurper.parseText(json)

        documents.each{ document ->
            document.remove('_id')
            document.lastUpdated = Date.parse("yyyy-MM-DD HH:mm:ss.sss", document.lastUpdated.toString().replaceAll(/[TZ]+/, ' '))
            document.dateCreated = Date.parse("yyyy-MM-DD HH:mm:ss.sss", document.dateCreated.toString().replaceAll(/[TZ]+/, ' '))
            def metric = new Metric(document)
            metric.save()
        }
    }
}
