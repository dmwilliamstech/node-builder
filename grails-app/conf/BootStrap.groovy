import node.builder.Image
import node.builder.InputFileChangeListener
import node.builder.Node
import node.builder.Manifest
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


        //dummy images
        (new Image(name: "Sample Image")).save()
        (new Image(name: "Another Sample Image")).save()
        (new Image(name: "Even More Sample Image")).save()
        //end dummy images
    }

    def destroy = {
    }
}
