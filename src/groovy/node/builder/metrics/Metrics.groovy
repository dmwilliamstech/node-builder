package node.builder.metrics

import grails.converters.JSON
import grails.util.Environment
import groovy.json.JsonOutput
import node.builder.Config
import org.codehaus.groovy.reflection.ReflectionUtils

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
class Metrics {
    def metricWithEventGroupMessageEventIdGroupIdParentId = { event, group, message, eventId, groupId, parentId, eventData, duration ->
        if(Environment.current == Environment.PRODUCTION || Metric.connected){
            try{
                def clazz = ReflectionUtils.getCallingClass(3)
                clazz = clazz.getCanonicalName().find(/[\w]*\.[\w\$]*$/)
                def thread = Thread.currentThread().name
                def metric = new Metric(clazz: clazz,
                        thread: thread,
                        group: group.title,
                        message: message,
                        event: event.title,
                        eventId: eventId,
                        groupId: groupId,
                        parentId: parentId,
                        eventData: JSON.parse(JsonOutput.toJson(eventData)),
                        duration: duration
                )
                metric.save()
                log.debug "Logged metric $metric.id"
            }catch (e){
                log.warn "Unable to log metric $e.message"
            }
        }
    }


    def metricWithEventGroupMessageEventIdGroupId = { event, group, message, eventId, groupId, parentId   ->
        log.metric(event, group, message, eventId, groupId, parentId, [:], -1)
    }

    def metricWithEventGroupMessage = { event, group, message  ->
        log.metric(event, group, message, "", "", "", [:], -1)
    }

    private Metrics(){
        log.info "Initializing Metrics system"
        def metrics = [metricWithEventGroupMessage, metricWithEventGroupMessageEventIdGroupId, metricWithEventGroupMessageEventIdGroupIdParentId]
        metrics.each{metric ->
            org.apache.commons.logging.impl.SLF4JLog.metaClass.metric << metric
            log.metaClass.metric << metric
        }
    }

    static def timeStringFromMilliseconds(millis) {
        int second = (millis / 1000d)
        int minute = (millis / (1000d * 60d)) % 60d
        int hour = (millis / (1000d * 60d * 60d)) % 24d
        millis = (millis / 1000 - second) * 1000
        def averageDuration = String.format("%02d:%02d:%02d.%d", hour, minute, second, (int)millis)
        averageDuration
    }

    static initialize(){
        new Metrics()
        Config.globalConfig.put("metrics.initialized", true)
    }
}
