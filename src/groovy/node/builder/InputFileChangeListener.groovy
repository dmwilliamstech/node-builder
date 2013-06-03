package node.builder

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

/**
 *
 */
class InputFileChangeListener implements DirectoryWatcher.FileChangeListener {
    void onChange(File file) {

    }

    void onNew(File file) {
        try{
            log.info "File added ${file.absolutePath}"
            def json = (new JsonSlurper()).parseText(file.text)

            log.info "Found ${json.applications.size()} application entry(s)"
            json.applications.each { application ->

                def domain = new Application(application)
                domain.save()

            }
        } catch(Exception e){
            log.error "Failed to load file ${file.path} caused by ${e}"
            e.printStackTrace()
        }
    }
}
