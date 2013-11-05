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



import grails.util.Environment
import node.builder.*
import node.builder.bpm.ProcessEngineFactory
import node.builder.metrics.MetricEvents
import node.builder.metrics.MetricGroups
import node.builder.metrics.Metrics
import node.builder.virt.OpenStackConnection
import org.activiti.engine.ProcessEngine
import org.activiti.engine.RepositoryService
import org.codehaus.groovy.grails.compiler.DirectoryWatcher
import org.codehaus.groovy.runtime.StackTraceUtils

class BootStrap {
    def grailsApplication


    InstanceService instanceService
    ImageService imageService
    FlavorService flavorService

    def init = { servletContext ->
        Metrics.initialize()
        loadConfig()
        if(Environment.currentEnvironment != Environment.PRODUCTION){
            loadSecurity()
        }
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
            log.info "Connecting to OpenStack server"
            flavorService.loadFlavors(OpenStackConnection.getConnection())
            imageService.loadImages(OpenStackConnection.getConnection())
            instanceService.loadInstances(OpenStackConnection.getConnection())
        }catch(Exception e){
            log.error "Failed to connect to and load OpenStack data ${StackTraceUtils.extractRootCause(e).getMessage()}"
        }

        log.metric(MetricEvents.START, MetricGroups.UP_TIME, "startup complete")
        log.info("Node Builder startup complete")
    }

    def loadProjectTypes() {
        ProjectTypeEnum.values().each{projectTypeEnum ->
            def projectType = ProjectType.findByName(projectTypeEnum.name)?: new ProjectType(name: projectTypeEnum.name)
            projectType.save()
        }
    }

    def loadSecurity() {
        log.info "Loading jdbc based security"

        def muppets = "muppets"
        def fraggles = "fraggles"

        def userRole = SecRole.findByAuthority('ROLE_USERS') ?: new SecRole(authority: 'ROLE_USERS').save(failOnError: true)
        def adminRole = SecRole.findByAuthority('ROLE_ADMINS') ?: new SecRole(authority: 'ROLE_ADMINS').save(failOnError: true)
        def nbAdminRole = SecRole.findByAuthority('ROLE_NBADMINS') ?: new SecRole(authority: 'ROLE_NBADMINS').save(failOnError: true)
        def adminUser = SecUser.findByUsername('admin') ?: new SecUser(
                username: 'admin',
                password: 'admin',
                enabled: true)

        adminUser.addToOrganizations(muppets)
        adminUser.save(failOnError: true)

        if (!adminUser.authorities.contains(adminRole)) {
            SecUserSecRole.create adminUser, adminRole
            SecUserSecRole.create adminUser, nbAdminRole
        }

        ["gobo", "mokey"].each { username ->
            def gogoUser = SecUser.findByUsername(username) ?: new SecUser(
                    username: username,
                    password: username,
                    enabled: true)

            gogoUser.addToOrganizations(fraggles)
            gogoUser.save(failOnError: true)

            if (!gogoUser.authorities.contains(adminRole)) {
                SecUserSecRole.create gogoUser, adminRole
            }
        }
    }

    def destroy = {
    }

    def loadConfig() {
        try{
            def config = Config.getGlobalConfig()
            def masterName = config.get("puppet.name")
            def master = Master.findByName(masterName)
            if(master == null)
                master = new Master()
            master.name = masterName
            master.hostname = config.get("puppet.hostname")
            master.username = config.get("puppet.username")
            master.privateKey = config.get("puppet.privateKey")
            master.remotePath = config.get("puppet.remote.path")

            master.save(failOnError: true)

            OpenStackConnection.createConnection(
                config.get("openstack.url"),
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
