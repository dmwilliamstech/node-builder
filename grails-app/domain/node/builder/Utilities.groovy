package node.builder

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass


class Utilities {
    def grailsApplication

    def serializeDomain(domain){
        def domainClass = grailsApplication.getDomainClass(domain.class.name)

        def map = new HashMap()
        domainClass.persistentProperties.each { property ->
            map[property.name.toString()] = domain[property.name].toString()
        }

        return map
    }
}
