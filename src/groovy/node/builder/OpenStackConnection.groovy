/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package node.builder

import grails.converters.JSON
import groovy.transform.Synchronized
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient


class OpenStackConnection extends Retryable {

    def hostname
    def username
    def password
    def tenantId
    def keyId
    RESTClient compute
    def token
    def defaultFlavorId
    String adminUrl
    def handler = {exception ->
        log.warn "Failed to connect to OpenStack"
        if(exception.getResponse().getStatus() == 401){
            log.warn "Authentication failed, let's reconnect and try again"
            this.disconnect()
            this.connect()
        }
    }


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
                body : [auth:[passwordCredentials:[username: this.username, password:this.password], tenantId: this.tenantId]],
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

    def disconnect(){
        this.compute.shutdown()
        this.compute = null
    }

    def images(){
        return objects("images")
    }

    def instances(){
        return objects("servers")
    }


    @Synchronized
    def delete(instanceId){
        if(this.compute == null)
            this.connect()

        def resp
        try {
            retry(handler, groovyx.net.http.HttpResponseException, 1){
                compute.delete( path : "servers/${instanceId}",
                    headers : ['X-Auth-Token' : token])
            }
        } catch (groovyx.net.http.HttpResponseException e) {
            resp = e.getResponse()
            if(resp.getStatus() >= 400){
                def error = [error: [message: resp.data + " ${instanceId}"]]

                throw ("Instance delete failed ${error.error.message}")
            }
            throw e
        }


        return true
    }

    @Synchronized
    def launch(flavor, image, instanceName){
        if(this.compute == null)
            this.connect()

        def resp
        try {
            retry(handler, groovyx.net.http.HttpResponseException, 1){
                resp = compute.post( path : 'servers',
                                 contentType : 'application/json',
                                 body :  [server: [flavorRef: ((flavor && flavor >= defaultFlavorId) ? flavor : defaultFlavorId), imageRef: image, key_name: this.keyId, name: instanceName]] ,
                                 headers : ['X-Auth-Token' : token])
            }
        } catch (groovyx.net.http.HttpResponseException e) {
            resp = e.getResponse()
            if(resp.getStatus() == 413){
                def error = [error: [message: resp.data.overLimit.message += " ${instanceName}"]]

                log.error("Instance Launch failed ${error.error.message}")
                return error
            }else if(resp.getStatus() >= 400){
                def error = [error: [message: resp.data.toString()]]

                log.error("Instance Launch failed ${error.error.message}")
                return error
            }
            throw e
        }

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
        return retry(handler, groovyx.net.http.HttpResponseException, 1){

            if(this.compute == null)
                this.connect()

            def objects = []

            def resp = compute.get( path : type + "/detail" ,
                headers : ['X-Auth-Token' : token] )

            return resp.data.get(type)
        }
    }

}
