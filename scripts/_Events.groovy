includeTargets << grailsScript("_GrailsEvents")
includeTargets << grailsScript("_GrailsSettings")

eventCleanStart = {
    println "| Cleaning non standard files"
    ant.delete('dir':'logs')
    ant.delete('dir':'out')
    ant.delete('dir':'target')
    println "| Cleaned directories"

    ant.delete{
        fileset(dir: System.properties['base.dir'], includes: "*.db")
    }

    println "| Deleted activiti db files"

    ant.delete{
        fileset(dir: System.properties['base.dir'], includes: "*.pp")
    }

    println "| Deleted generated puppet files"
    System.exit(0)
}