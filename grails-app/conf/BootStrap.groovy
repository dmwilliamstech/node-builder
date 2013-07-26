import grails.converters.JSON
import node.builder.FlavorService
import node.builder.Image
import node.builder.ImageService
import node.builder.InputFileChangeListener
import node.builder.Instance
import node.builder.InstanceService
import node.builder.ManifestService
import node.builder.Master
import node.builder.Node
import node.builder.Manifest
import node.builder.OpenStackConnection
import node.builder.Repository
import node.builder.SecRole
import node.builder.SecUser
import node.builder.SecUserSecRole
import node.builder.Utilities
import node.builder.bpm.ProcessEngineFactory
import org.activiti.engine.HistoryService
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.runtime.Execution
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

class BootStrap {
    def grailsApplication
    def springSecurityService

    InstanceService instanceService
    ImageService imageService
    FlavorService flavorService

    def init = { servletContext ->
        loadConfig()
        loadSecurity()


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
        directoryWatcher.addListener(new InputFileChangeListener())
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
            def configFile = new File("${System.getenv("HOME")}/.opendx/config")
            if(!configFile.exists())
                configFile = new File("/etc/node-builder.conf")

            ConfigObject config = new ConfigSlurper().parse(new URL("file://${configFile.absolutePath}")).flatten()
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

            log.info("Checking for repositories to monitor")
            Repository.where { id > 0l }.deleteAll()
            config.get("monitor.repos")?.each{ repoData ->
                log.info("Found repository (${repoData.name}) in config, updating system")
                def repoInstance = Repository.findByName(repoData.name) ?: new Repository(name: repoData.name)
                repoInstance.localPath = repoData.localPath
                repoInstance.remotePath = repoData.remotePath
                repoInstance.save()
            }
        }catch (e){
            log.warn("Failed to load config file - " + e.getMessage())
            e.printStackTrace()
        }
    }

}
