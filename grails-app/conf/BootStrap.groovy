import grails.converters.JSON
import node.builder.FlavorService
import node.builder.Image
import node.builder.ImageService
import node.builder.InputFileChangeListener
import node.builder.Instance
import node.builder.InstanceService
import node.builder.Master
import node.builder.Node
import node.builder.Manifest
import node.builder.OpenStackConnection
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

class BootStrap {
    def grailsApplication
    InstanceService instanceService
    ImageService imageService
    FlavorService flavorService

    def init = { servletContext ->

        loadConfig()

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

        log.info "should be running"

        try{
            imageService.loadImages(OpenStackConnection.getConnection())
            instanceService.loadInstances(OpenStackConnection.getConnection())
            flavorService.loadFlavors(OpenStackConnection.getConnection())
        }catch(Exception e){
            log.error "Failed to load OpenStack data - ${e}"
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
            def master = new Master()
            master.name = config.get("master.name")
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
        }
    }

}
