package node.builder

import org.h2.jdbc.JdbcSQLException
import org.hibernate.exception.GenericJDBCException
import org.quartz.core.QuartzScheduler



class UpdateFromOpenStackJob {
    ImageService imageService
    InstanceService instanceService
    def quartzScheduler

    static triggers = {
        simple repeatInterval: 10000l // execute job once in 5 seconds
    }

    def execute() {
        log.debug "updating openstack data"
        try{
            if(OpenStackConnection.getConnection()){
                imageService.loadImages(OpenStackConnection.getConnection())
                instanceService.loadInstances(OpenStackConnection.getConnection())
            }
        } catch (org.h2.message.DbException e){}
          catch (JdbcSQLException e){}
          catch (Exception e){}
          finally {
              def jobKeys = quartzScheduler.getJobKeys()
              jobKeys.each { jobKey ->
                  quartzScheduler.deleteJob(jobKey)
              }
          }
    }
}
