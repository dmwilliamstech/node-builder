import grails.converters.JSON
import node.builder.Image
import node.builder.InputFileChangeListener
import node.builder.Instance
import node.builder.Master
import node.builder.Node
import node.builder.Manifest
import node.builder.OpenStackConnection
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

class BootStrap {
    def grailsApplication

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
            loadImages(OpenStackConnection.getConnection())
            loadInstances(OpenStackConnection.getConnection())
        }catch(Exception e){
            log.error "Failed to load OpenStack data - ${e}"
        }
    }

    def destroy = {
    }

    def loadConfig() {
        ConfigObject config = new ConfigSlurper().parse(new URL("file://${System.getenv("HOME")}/.opendx/config")).flatten()
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
            config.get("openstack.key.id")
        )
    }

    def loadImages(connection){

        def images = connection.images()
        images.each { image ->
            def imageData = image.image
            def imageInstance = new Image(name: imageData.name, imageId: imageData.id, progress: imageData.progress, minDisk: imageData.minDisk, minRam: imageData.minRam, status: imageData.minRam)
            imageInstance.save()
        }
    }

    def loadInstances(connection){
        def instances = connection.instances()
        instances.each { instance ->
            def server = instance.server

            def instanceInstance = new Instance(
                    name: server.name,
                    status: server.status,
                    hostId: server.hostId,
                    privateIP: server.addresses.private[0].addr,
                    keyName: server.key_name.toString(),
                    flavorId: server.flavor.id,
                    instanceId: server.id,
                    userId: server.user_id,
                    tenantId: server.tenant_id,
                    progress: server.progress,
                    configDrive: server.config_drive,
                    metadata: (server.metadata as JSON).toString(),
                    image: Image.findByImageId(server.image.id)
            )
            instanceInstance.save(failOnError: true)
        }
    }
}
