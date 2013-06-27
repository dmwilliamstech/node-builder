package node.builder

import groovy.json.JsonSlurper
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

/**
 *
 */
class InputFileChangeListener implements DirectoryWatcher.FileChangeListener {

    def persistenceInterceptor

    void onChange(File file) {

    }

    void onNew(File file) {
        try{
            log.info "File added ${file.absolutePath}"
            def json = (new JsonSlurper()).parseText(file.text)
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
            def configurations = application.remove("configurations")

            def domain = new Application(application)
            if(node)
                domain.node = node
            else
                domain.node = Node.first()

            Application.withTransaction{
                if(domain.errors.hasErrors())
                    throw new Exception(domain.errors.toString())
                domain.save()
            }
            if(configurations)
                loadConfigurations(domain, configurations, ApplicationConfiguration)
        }
    }

    private void loadNodes(nodes){
        log.info "Found ${nodes.size()} node entry(s)"
        nodes.each { node ->
            def applications = node.remove("applications")
            def configurations = node.remove("configurations")

            def domain = new Node(node)
            Node.withTransaction {
                if(domain.errors.hasErrors())
                    throw new Exception(domain.errors.toString())
                domain.save()
            }
            if(applications)
                loadApplications(domain, applications)

            if(configurations)
                loadConfigurations(domain, configurations, NodeConfiguration)
        }
    }

    private def loadConfigurations = {parent, configurations, Class configurationType ->
        log.info "Found ${configurations.size()} configuration entry(s)"
        configurations.each { configuration ->

            def domain = configurationType.newInstance()
            try{
            configurationType.withTransaction{
                domain.name = configuration.name
                domain.value = configuration.value
                domain.description = configuration.description
                if(parent instanceof Application)
                    domain.application = parent
                else
                    domain.node = parent

                domain.save()
                if(domain.errors.hasErrors())
                    throw new Exception(domain.errors.toString())

            }
            }catch(e){
                log.error "Error loading ${configuration.name}"
                e.printStackTrace()
            }
        }

    }
}
