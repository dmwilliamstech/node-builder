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
    def reference = "git rev-parse HEAD".execute().text
    new File("grails-app/conf/reference.txt").write(reference)
    println "| Current git reference is ${reference}"
    "grails refresh-dependencies grails-app/conf/dependency-report.xml".execute()
    println "| Created dependency report"
}