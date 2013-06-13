import node.builder.Image
import node.builder.InputFileChangeListener
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

        loadImages()
    }

    def destroy = {
    }

    def loadImages(){
        def connection = new OpenStackConnection("107.2.16.122", "admin", "stack", "2ba2d60c5e8d4d1b86549d988131fe48")
        def images = connection.images()
        images.each { image ->
            def imageData = image.image
            def imageInstance = new Image(name: imageData.name, imageId: imageData.id, progress: imageData.progress, minDisk: imageData.minDisk, minRam: imageData.minRam, status: imageData.minRam)
            imageInstance.save(failOnError: true)
        }
    }

    def loadInstances(){

    }
}
