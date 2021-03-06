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

import grails.plugins.springsecurity.SecurityConfigType
import grails.util.Environment


// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format
if(Environment.currentEnvironment == Environment.PRODUCTION){
        grails.config.locations = [
            "file:${userHome}/.opendx/node-builder-datasource.groovy",
            "file:${userHome}/.opendx/node-builder-ldap.groovy",
            "file:/etc/node-builder-datasource.groovy",
            "file:/etc/node-builder-ldap.groovy"]
}

grails.project.groupId = "org.codice.opendx" // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"

grails.converters.json.pretty.print = true
grails.converters.default.pretty.print = true
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    debug 'org.codehaus.groovy.grails.plugins.springsecurity'
    debug 'org.springframework.ldap'

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    info   'node.builder'

//    root {
//        info 'stdout', 'file'
//    }
}


grails.config.defaults.locations = [KickstartResources]
script.install.directory = "~/.opendx/node-builder"

environments {
    test {
        // Added by the Spring Security Core plugin:
        grails.plugins.springsecurity.userLookup.userDomainClassName = 'node.builder.SecUser'
        grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'node.builder.SecUserSecRole'
        grails.plugins.springsecurity.authority.className = 'node.builder.SecRole'

        grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
        grails.plugins.springsecurity.interceptUrlMap = [
                '/login/**':               ['IS_AUTHENTICATED_ANONYMOUSLY'],
                '/administration/**':      ['ROLE_NBADMINS'],
                '/**':                     ['ROLE_ADMINS','ROLE_USERS','IS_AUTHENTICATED_FULLY']

        ]

    }

    development {
        // Added by the Spring Security Core plugin:
        grails.plugins.springsecurity.userLookup.userDomainClassName = 'node.builder.SecUser'
        grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'node.builder.SecUserSecRole'
        grails.plugins.springsecurity.authority.className = 'node.builder.SecRole'

        grails.plugins.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap
        grails.plugins.springsecurity.interceptUrlMap = [
                '/login/**':               ['IS_AUTHENTICATED_ANONYMOUSLY'],
                '/administration/**':      ['ROLE_NBADMINS'],
                '/**':                     ['ROLE_ADMINS','ROLE_USERS','IS_AUTHENTICATED_FULLY']
        ]
    }
}
