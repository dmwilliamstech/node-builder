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

import node.builder.*
import node.builder.bpm.ProcessEngineFactory
import org.activiti.engine.ProcessEngine
import org.activiti.engine.RepositoryService
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

class BootStrap {
    def grailsApplication


    InstanceService instanceService
    ImageService imageService
    FlavorService flavorService

    def init = { servletContext ->
        loadConfig()
        loadSecurity()
        loadProjectTypes()
        loadProcessDefinitions()

        if(Node.count.is(0)){
            log.info "Creating default Node"
            def nodeType = new Node(name: "Default", description: "Default Container for nodetype less Applications")
            nodeType.save()
        }

        def expandedPath = grailsApplication.config.script.install.directory.replace("~",System.getProperty("user.home"))
        def installDirectory = new File(expandedPath)

        if(!installDirectory.exists())
            installDirectory.mkdirs()
        log.info "Adding watcher for drop folder ${installDirectory.getPath()} "

        def directoryWatcher = new DirectoryWatcher()
        directoryWatcher.addWatchDirectory(installDirectory, "json")
        directoryWatcher.addListener(InputFileChangeListener.getDefaultListener(installDirectory))
        directoryWatcher.start()


        try{
            imageService.loadImages(OpenStackConnection.getConnection())
            instanceService.loadInstances(OpenStackConnection.getConnection())
            flavorService.loadFlavors(OpenStackConnection.getConnection())
        }catch(Exception e){
            log.error "Failed to load OpenStack data - ${e}"
        }

        log.info("Node Builder startup complete")
    }

    def loadProjectTypes() {
        def projectType = ProjectType.findByName("GIT Repository")?: new ProjectType(name: "GIT Repository")
        projectType.save()
    }

    def loadSecurity() {
        def userRole = SecRole.findByAuthority('ROLE_USER') ?: new SecRole(authority: 'ROLE_USER').save(failOnError: true)
        def adminRole = SecRole.findByAuthority('ROLE_ADMIN') ?: new SecRole(authority: 'ROLE_ADMIN').save(failOnError: true)
        def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
                username: 'admin',
                password: 'admin',
                enabled: true).save(failOnError: true)

        if (!adminUser.authorities.contains(adminRole)) {
            SecUserSecRole.create adminUser, adminRole
        }

    }

    def destroy = {
    }

    def loadConfig() {
        try{
            def config = Config.getGlobalConfig()
            def masterName = config.get("master.name")
            def master = Master.findByName(masterName)
            if(master == null)
                master = new Master()
            master.name = masterName
            master.hostname = config.get("master.hostname")
            master.username = config.get("master.username")
            master.privateKey = config.get("master.privateKey")
            master.remotePath = config.get("master.remote.path")

            master.save(failOnError: true)

            OpenStackConnection.createConnection(
                config.get("openstack.hostname"),
                config.get("openstack.username"),
                config.get("openstack.password"),
                config.get("openstack.tenant.id"),
                config.get("openstack.key.id"),
                config.get("openstack.flavor.id")
            )

        }catch (e){
            log.warn("Failed to load config file - " + e.getMessage())
            e.printStackTrace()
        }
    }

    def loadProcessDefinitions(){
        ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine("loaded")
        RepositoryService repositoryService = processEngine.getRepositoryService();

        [
            "resources/deprovision_instance.bpmn20.xml",
            "resources/provision_instance.bpmn20.xml",
                "resources/monitor_git.bpmn20.xml",
        ].each{ resourceLocation ->
            org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resourceLocation)
                .deploy();

            log.info("Loaded process definition ${resourceLocation} with id (${deployment.id})")
        }
    }


    def getJobXml(){
        return """\
<?xml version='1.0' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders/>
  <publishers/>
  <buildWrappers/>
</project>
"""
    }
}
