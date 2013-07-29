package node.builder

class UpdateFromOpenStackJob {
    ImageService imageService
    InstanceService instanceService
    def quartzScheduler
    def sessionFactory

    static triggers = {
        simple repeatInterval: 10000l // execute job once in 5 seconds
    }

    def execute() {
        log.debug "updating openstack data"


//        if(OpenStackConnection.getConnection() && sessionFactory.currentSession.isConnected()){
//            imageService.loadImages(OpenStackConnection.getConnection())
//            instanceService.loadInstances(OpenStackConnection.getConnection())
//        }

    }
}
