package node.builder

import grails.converters.JSON
import groovy.transform.Synchronized
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient


class OpenStackConnection {

    def hostname
    def username
    def password
    def tenantId
    def keyId
    RESTClient compute
    def token
    def defaultFlavorId
    String adminUrl

    private static OpenStackConnection connection


    static def createConnection(hostname, username, password, tenantId, keyId, defaultFlavorId){
        if(connection == null)
            connection = new OpenStackConnection(hostname, username, password, tenantId, keyId, defaultFlavorId)
        assert connection != null
        return connection
    }

    static def getConnection(){
        return connection
    }

    private def OpenStackConnection(hostname, username, password, tenantId, keyId, defaultFlavorId){
        this.hostname = hostname
        this.username = username
        this.password = password
        this.tenantId = tenantId
        this.keyId = keyId
        this.defaultFlavorId = defaultFlavorId

    }


    def connect(){
        def keystone = new RESTClient( "http://${this.hostname}:5000/v2.0/" )

        def resp = keystone.post( path : 'tokens',
                body : [auth:[passwordCredentials:[username: "admin", password:"stack"], tenantId: "2ba2d60c5e8d4d1b86549d988131fe48"]],
                contentType : 'application/json' )

        this.token = resp.data.access.token.id

        for (endpoint in resp.data.access.serviceCatalog) {
            if (endpoint.type == 'compute' ) {
                                                                      //make sure openstack doesn't redirect us
                this.adminUrl = (endpoint.endpoints.adminURL[0] + "/").replaceAll("\\/\\/.*\\:", "//${hostname}:")
                this.compute = new RESTClient(this.adminUrl)
            }
        }
    }

    def images(){
        return objects("images")
    }

    def instances(){
        return objects("servers")
    }

    @Synchronized
    def launch(flavor, image, instanceName){
        if(this.compute == null)
            this.connect()

        def resp = compute.post( path : 'servers',
                             contentType : 'application/json',
                             body :  [server: [flavorRef: (flavor ?: defaultFlavorId), imageRef: image, key_name: this.keyId, name: instanceName]] ,
                             headers : ['X-Auth-Token' : token])

        HttpResponseDecorator details = compute.get ( path : "servers/" + resp.data.server.id,
                headers : [ 'X-Auth-Token' : token] )

        return details.data
    }

    @Synchronized
    def flavors(){
        return objects("flavors")
    }

    @Synchronized
    private def objects(type){
        if(this.compute == null)
            this.connect()

        def objects = []

        def resp = compute.get( path : type + "/detail" ,
                headers : ['X-Auth-Token' : token] )

        return resp.data.get(type)
    }

}
