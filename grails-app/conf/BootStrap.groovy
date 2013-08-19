import grails.build.logging.GrailsConsole
import grails.converters.JSON
import node.builder.*
import node.builder.bpm.ProcessEngineFactory
import org.activiti.engine.ProcessEngine
import org.activiti.engine.RepositoryService
import org.codehaus.groovy.grails.compiler.DirectoryWatcher
import org.codehaus.groovy.grails.io.support.ClassPathResource
import org.codehaus.groovy.reflection.ReflectionUtils
import org.fusesource.jansi.AnsiConsole
import org.joda.time.DateTime

class BootStrap {
    def grailsApplication


    InstanceService instanceService
    ImageService imageService
    FlavorService flavorService

    def init = { servletContext ->

        org.apache.commons.logging.impl.SLF4JLog.metaClass.foo = { message ->
            def clazz = ReflectionUtils.getCallingClass(3)
            def className = clazz.getCanonicalName().find(/[\w]*\.[\w\$]*$/)
            AnsiConsole.out.append("\r${DateTime.now().toString("yyyy-MM-dd hh:mm:ss,SSS")} [${Thread.currentThread().name}] FOO   ${className} ${message}")
            AnsiConsole.out.flush()
        }

        loadConfig()
        loadSecurity()
        loadProcessDefinitions()
        addJenkinsAgentManifest()

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
                repoInstance.workflowKey = repoData.workflowKey
                repoInstance.save()
            }
        }catch (e){
            log.warn("Failed to load config file - " + e.getMessage())
            e.printStackTrace()
        }
    }

    def loadProcessDefinitions(){
        ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine("loaded")
        RepositoryService repositoryService = processEngine.getRepositoryService();

        [
            "resources/monitor_git.bpmn20.xml",
            "resources/deprovision_instance.bpmn20.xml",
            "resources/provision_instance.bpmn20.xml",
            "resources/create_run_jenkins.bpmn20.xml",
            "resources/monitor_git_create_job_create_issue.bpmn20.xml"
        ].each{ resourceLocation ->
            org.activiti.engine.repository.Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(resourceLocation)
                .deploy();

            log.info("Loaded process definition ${resourceLocation} with id (${deployment.id})")
        }
    }

    def addJenkinsAgentManifest(){
        def manifest = Manifest.findByName("jenkins-agent")
        if(manifest == null){
            manifest = new Manifest(name: "jenkins-agent")
            def resource = new ClassPathResource("resources/jenkins-agent.json")
            def json = resource.file.text

            manifest.manifestAsJSON = json
            manifest.description = "Manifest for creating a jenkins agent node"
            manifest.manifest = JSON.parse(json)

            manifest.save()
        }
    }

}
