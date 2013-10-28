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

includeTargets << grailsScript("_GrailsEvents")
includeTargets << grailsScript("_GrailsSettings")

eventCleanStart = {
    println "| Cleaning non standard files"
    ant.delete('dir':'logs')
    ant.delete('dir':'out')
    ant.delete('dir':'target')
    ant.delete('dir':'tmp')
    println "| Cleaned directories"

    ant.delete{
        fileset(dir: System.properties['base.dir'], includes: "*.db")
    }

    println "| Deleted activiti db files"

    ant.delete{
        fileset(dir: System.properties['base.dir'], includes: "*.pp")
    }

    println "| Deleted generated puppet files"

    ant.delete{
        fileset(dir: System.properties['base.dir'], includes: "grails-app/conf/dependency-report.xml")
    }
    println "| Deleted dependency report"

    ant.delete{
        fileset(dir: System.properties['base.dir'], includes: "grails-app/conf/reference.txt")
    }
    println "| Deleted reference file"
}

eventCompileStart = { kind ->
    projectCompiler.srcDirectories << "$basedir/test/groovy"
    println "| Added test/groovy to Source Directories"
    def reference = "git rev-parse HEAD".execute().text
    new File("grails-app/conf/reference.txt").write(reference)
    println "| Current git reference is ${reference}"
    "grails refresh-dependencies grails-app/conf/dependency-report.xml".execute()
    println "| Created dependency report"
}
eventAllTestsStart = {
    classLoader.addURL(new File("$basedir/test/groovy").toURL())
    println "| Added test/groovy to ClassPath"
}