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

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.support.aware.GrailsApplicationAware
import org.codehaus.groovy.runtime.StackTraceUtils

class Utilities implements GrailsApplicationAware {
    def gApp

    def isSerializable(object){
        try{
            if(object.class?.name =~ /javassist/ && object.class?.name =~ /node\.builder/)
                return false
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(object);
            return true
        }catch( e){
            return false
        }
    }

    def serializeDomain(domain, level = 0){
        if(domain == null || level > 1)
            return null


        def domainClass = new DefaultGrailsDomainClass(Class.forName(domain.class.name.replaceAll(/\_\$\$\_javassist.*/, ""), true, Thread.currentThread().contextClassLoader))

        def map = new HashMap()
        if(domainClass == null)
            return map


        domainClass.persistentProperties.each { property ->
            if(domain[property.name] == null || isSerializable(domain[property.name])){
                if(domain[property.name] != null )
                    map[property.name.toString()] = domain[property.name].toString()
            }else if(domain[property.name] instanceof java.util.LinkedHashSet || domain[property.name] instanceof org.hibernate.collection.PersistentSet){
                def list = []
                domain[property.name].each{ d ->
                    if(isSerializable(d)){
                        list.add(d)
                    }else{
                        def add = serializeDomain(d, level + 1)
                        list.add( add)
                    }
                    return list
                }
                map[property.name.toString()] = list
            }else{
                map[property.name.toString()] = serializeDomain(domain[property.name], level + 1)
            }
        }

        map.id = domain.id

        return map
    }

    void setGrailsApplication(GrailsApplication grailsApplication) {
        this.gApp = grailsApplication
    }

    def wasCalledFromMethodWithName(methodName){
        def marker = new Throwable()
        def stackTrace = StackTraceUtils.sanitize(marker).stackTrace
        return stackTrace.find() { e ->
            e.methodName == methodName
        }  != null
    }

    def wasCalledFromClassAndMethodWithName(className, methodName){
        def marker = new Throwable()
        def stackTrace = StackTraceUtils.sanitize(marker).stackTrace
        return stackTrace.find() { e ->
            e.methodName == methodName && e.className == className
        } != null
    }
}
