grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve true // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()


        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://artifacts.alfresco.com/nexus/content/repositories/activiti-releases/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        // runtime 'mysql:mysql-connector-java:5.1.20'
        test "org.gebish:geb-junit4:0.9.0"
        test "org.seleniumhq.selenium:selenium-support:2.33.0"


        test("org.seleniumhq.selenium:selenium-chrome-driver:2.33.0")
        test( "com.github.detro.ghostdriver:phantomjsdriver:1.0.1" ) {
            transitive = false
        }
        test("org.seleniumhq.selenium:selenium-firefox-driver:2.33.0")
        compile 'org.apache.ant:ant-jsch:1.8.4'
        compile 'com.jcraft:jsch:0.1.46'
        compile 'org.tmatesoft.svnkit:svnkit:1.7.8'
        compile 'org.activiti:activiti-engine:5.13'
        compile 'org.eclipse.jgit:org.eclipse.jgit:3.0.0.201306101825-r'

    }

    plugins {
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.8.3"
        runtime ":resources:1.1.6"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        build ":tomcat:$grailsVersion"

        runtime ":database-migration:1.2.1"
        runtime ":rest:0.7"
        runtime ":content-buffer:1.0.1"

//        cacheDir()
        test ":geb:0.9.0"

        compile ":maven-publisher:0.8.1"
        compile ':cache:1.0.1'
        compile 'org.grails.plugins:json-rest-api:1.0.11'
        compile ":quartz:1.0-RC9"
        compile ':spring-security-core:1.2.7.3'
    }
}

grails.project.dependency.distribution = {
    localRepository = "~/.m2"
    remoteRepository(id:"codiceSnapshots", url:"http://artifacts.codice.org/content/repositories/snapshots/")
    remoteRepository(id:"codiceReleases", url:"http://artifacts.codice.org/content/repositories/releases/")
}