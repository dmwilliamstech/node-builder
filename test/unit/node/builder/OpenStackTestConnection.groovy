package node.builder

import groovy.json.JsonSlurper
import groovy.transform.Synchronized
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource


class OpenStackTestConnection {

    private String resourcePath
    private static OpenStackTestConnection connection


    static def createConnection(resourcePath){
        return new OpenStackTestConnection(resourcePath)
    }

    static def getConnection(){
        return connection
    }

    private def OpenStackTestConnection(resourcePath){
        this.resourcePath = resourcePath
    }


    def connect(){

    }

    def images(){
        Resource resource = new ClassPathResource(resourcePath)
        def file = resource.getFile()
        assert file.exists()
        return new JsonSlurper().parseText(file.text)
    }

    def instances(){
        Resource resource = new ClassPathResource(resourcePath)
        def file = resource.getFile()
        assert file.exists()
        return new JsonSlurper().parseText(file.text)
    }

    @Synchronized
    def launch(flavor, image, instanceName){
        Resource resource = new ClassPathResource(resourcePath)
        def file = resource.getFile()
        assert file.exists()
        return new JsonSlurper().parseText(file.text)
    }

    def delete(instanceId){
        return true
    }

}
