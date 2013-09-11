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

import groovy.io.FileType
import groovy.json.JsonSlurper
import node.builder.exceptions.NotDirectoryException
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

/**
 *
 */
class InputFileChangeListener implements DirectoryWatcher.FileChangeListener {

    static InputFileChangeListener listener
    File directory

    static InputFileChangeListener getDefaultListener(File directory){
        if(listener == null){
            listener = new InputFileChangeListener(directory)
            //load existing files

            if(!directory.isDirectory())
                throw new NotDirectoryException(directory.path)
            directory.eachFileRecurse(FileType.FILES) { file ->
                listener.loadFile(file)
            }
        }
        return listener
    }

    def InputFileChangeListener(File directory){
        this.directory = directory
    }

    void onChange(File file) {
        loadFile(file)
    }

    void onNew(File file) {
        loadFile(file)
    }

    private void loadFile(File file){
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
            def domain
            Application.withTransaction{
                 domain = Application.findByName(application.name)
                if(domain==null)
                    domain = new Application()
                domain.name = application.name
                domain.description = application.description
                domain.priority = application.priority
                domain.flavorId = application.flavorId

                if(node)
                    domain.node = node
                else
                    domain.node = Node.first()


                if(domain.errors.hasErrors())
                    throw new Exception(domain.errors.toString())
                domain.save(failOnError:true)
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
            def domain
            Node.withTransaction {
                domain = Node.findByName(node.name)
                if(domain == null){
                    domain = new Node()
                }else{
                    def apps = []
                    domain.applications.each{application ->
                        apps.add(application)
                        application.delete()
                    }
                    apps.each { app ->
                        domain.removeFromApplications(app)
                    }
                    assert domain.applications.size() == 0

                    def configs = []
                    domain.configurations.each{configuration ->
                        configs.add(configuration)
                        configuration.delete()
                    }
                    configs.each { config ->
                        domain.removeFromConfigurations(config)
                    }
                    assert domain.configurations.size() == 0
                }

                domain.name = node.name
                domain.description = node.description
                domain.flavorId = node.flavorId


                if(domain.errors.hasErrors())
                    throw new Exception(domain.errors.toString())
                domain.save(failOnError:true)
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
            try{
            configurationType.withTransaction{
                def domain = configurationType.findByName(configuration.name)
                if(domain == null){
                    domain = configurationType.newInstance()
                }

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
