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
            print json
            if(json.applications == null && json.nodes == null)
                throw new Exception("Input file must contain atleast one application or node object")

            if(json.applications){

                loadApplications(null, json.applications)

            }

            if(json.nodes){

                loadNodes(json.nodes)

            }
        } catch(Exception e){
            log.error "Failed to load file ${file.path} caused by ${e}"
        }
    }


    private void loadApplications(node, applications){
        log.info "Found ${applications.size()} application entry(s)"
        applications.each { application ->

            def domain = new Application(application)
            if(domain.errors.hasErrors())
                throw new Exception(domain.errors.toString())
            domain.save()

            if(node)
                node.addToApplications(domain)

        }
    }

    private void loadNodes(nodes){
        log.info "Found ${nodes.size()} node entry(s)"
        nodes.each { node ->
            log.error node
            def applications = node.remove("applications")
            log.error node
            def domain = new Node(node)
            if(domain.errors.hasErrors())
                throw new Exception(domain.errors.toString())
            domain.save()

            if(applications)
                loadApplications(domain, applications)
        }
    }
}
