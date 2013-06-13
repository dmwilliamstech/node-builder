import grails.converters.JSON
import node.builder.Image
import node.builder.InputFileChangeListener
import node.builder.Instance
import node.builder.Node
import node.builder.Manifest
import node.builder.OpenStackConnection
import org.codehaus.groovy.grails.compiler.DirectoryWatcher

class BootStrap {
    def grailsApplication

    def init = { servletContext ->
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
            def connection = new OpenStackConnection("107.2.16.122", "admin", "stack", "2ba2d60c5e8d4d1b86549d988131fe48")
            loadImages(connection)
            loadInstances(connection)
        }catch(Exception e){
            log.error "Failed to load OpenStack - ${e}"
        }
    }

    def destroy = {
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
                    keyName: server.key_name,
                    flavorId: server.flavor.id,
                    instanceId: server.id,
                    userId: server.user_id,
                    tenantId: server.tenant_id,
                    progress: server.progress,
                    configDrive: server.config_drive,
                    metadata: (server.metadata as JSON).toString(),
                    image: Image.findByImageId(server.image.id)
            )
            instanceInstance.save()
        }
    }
}
