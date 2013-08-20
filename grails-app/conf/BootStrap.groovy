import grails.util.Environment
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
        loadProcessDefinitions()
        runCqlScript("localhost", "test/data/schema-drop.txt")
        runCqlScript("localhost", "test/data/schema-create.txt")

        if (Environment.current == Environment.TEST) {
            bootstrapIntegrationTestData()
        }

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


    private runCqlScript(host, script) {
        def dsePath = System.getProperty('dsePath')?: "/usr/local"
        def cassandraCli = "$dsePath/bin/cassandra-cli -h ${host} -f"

        def cmd = "$cassandraCli $script"
        log.info "Running cassandra script with command: '$cmd'"
        def p = cmd.execute()
        def stderr = p.err?.text
        def stdout = p.text
        if (stderr) {
            log.error stderr
        } else {
            log.debug stdout
        }
    }

    private bootstrapIntegrationTestData()
    {
        def u1 = new User(
                username:  "matt",
                emailAddress: "matt@everywhere.com",
                firstName: "Matt",
                lastName: "Marvelous",
                address: "1 Main St",
                city: "Ellicott City",
                state: "MD",
                country: "USA",
                zip: "21043",
                phone: "+14105551212",
//                gender: Gender.MALE,
                birthDate: Date.parse("yyyy-MM-dd","1992-01-05")
        ).save()

        def u2 = new User(
                username:  "katie",
                emailAddress: "katie@everywhere.com",
                firstName: "Katie",
                lastName: "Courageous",
                address: "100 Avenue Del Sol",
                city: "Malaga",
                state: "Andalusia",
                country: "ESP",
                zip: "200034",
                phone: "+34923444121",
//                gender: Gender.FEMALE,
                birthDate: Date.parse("yyyy-MM-dd","1992-09-01")
        ).save()

        def u3 = new User(
                username:  "vicky",
                emailAddress: "vicky@everywhere.com",
                firstName: "Vicky",
                lastName: "Victorious",
                address: "1 Avenue Del Sol",
                city: "Olney",
                state: "MD",
                country: "USA",
                zip: "20832",
                phone: "+14105551212",
//                gender: Gender.FEMALE,
                birthDate: Date.parse("yyyy-MM-dd","1995-12-25")
        ).save()


//        def p1 = new Post(
//                title: "My very first post",
//                text: "Four score and seven years ago",
//                tags:  ["gettysburg"],
//                occurTime: Date.parse("yyyy-MM-dd","2012-07-01")
//        )
//        u1.addToPosts(p1)
//
//        def p2 = new Post(
//                title: "My second post",
//                text: "our fathers brought forth on this continent,",
//                tags:  ["gettysburg","fathers"],
//                occurTime: Date.parse("yyyy-MM-dd","2012-07-04")
//        )
//        u1.addToPosts(p2)
//
//        def p3 = new Post(
//                title: "Just getting started",
//                text: "a new nation, conceived in Liberty,",
//                tags:  ["gettysburg","liberty"],
//                occurTime: Date.parse("yyyy-MM-dd","2012-08-05")
//        )
//        u1.addToPosts(p3)
//
//        def p4 = new Post(
//                title: "Another first post",
//                text: "and dedicated to the proposition that all men are created equal.",
//                tags:  ["gettysburg","liberty","equality"],
//                occurTime: Date.parse("yyyy-MM-dd","2013-03-15")
//        )
//        u2.addToPosts(p4)
//
//
//        def c = new Comment(
//                text: "You tell um Abe!",
//                occurTime: Date.parse("yyyy-MM-dd","2012-07-03")
//        )
//        p1.addToComments(c)
//
//        c = new Comment(
//                text: "Oh yeah!",
//                occurTime: Date.parse("yyyy-MM-dd","2012-07-05")
//        )
//        p2.addToComments(c)
//
//        c = new Comment(
//                text: "Oh yeah, oh yeahhh!",
//                occurTime: Date.parse("yyyy-MM-dd","2012-08-01")
//        )
//        p2.addToComments(c)
//
//        c = new Comment(
//                text: "You can say that again",
//                occurTime: Date.parse("yyyy-MM-dd","2012-09-03")
//        )
//        p3.addToComments(c)
//
//        c = new Comment(
//                text: "Amen brother !!!",
//                occurTime: Date.parse("yyyy-MM-dd","2013-06-03")
//        )
//        p4.addToComments(c)
//
//        p1.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-03 08:10"), referrerType:  ReferrerType.DIRECT, ipAddress: "127.0.0.1"))
//        p1.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-03 11:20"), referrerType:  ReferrerType.SEARCH, referrerName: "Google", ipAddress: "127.0.0.1"))
//        p1.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-04 09:10"), referrerType:  ReferrerType.DIRECTORY, referrerName: "Citysearch", ipAddress: "127.0.0.1"))
//        p2.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-05 07:10"), referrerType:  ReferrerType.SOCIAL, referrerName: "Facebook", ipAddress: "127.0.0.1"))
//        p2.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-05 22:10"), referrerType:  ReferrerType.DIRECT, ipAddress: "127.0.0.1"))
//        p2.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-06 10:10"), referrerType:  ReferrerType.SEARCH, referrerName: "Google", ipAddress: "127.0.0.1"))
//        p3.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-07-01 08:10"), referrerType:  ReferrerType.DIRECTORY, referrerName: "Insiderpages", ipAddress: "127.0.0.1"))
//        p3.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-07-03 08:10"), referrerType:  ReferrerType.SOCIAL, referrerName: "Twitter", ipAddress: "127.0.0.1"))
//        p4.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-06-06 10:10"), referrerType:  ReferrerType.SEARCH, referrerName: "Bing", ipAddress: "127.0.0.1"))
//        p4.addToViews(new View(occurTime: Date.parse("yyyy-MM-dd HH:mm","2013-07-01 08:10"), referrerType:  ReferrerType.DIRECTORY, referrerName: "Yahoo!", ipAddress: "127.0.0.1"))

    }
}
