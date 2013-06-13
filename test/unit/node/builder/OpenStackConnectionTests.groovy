package node.builder

import grails.converters.JSON



class OpenStackConnectionTests {

    void testListImages() {
        def connection = new OpenStackConnection("localhost", "admin", "stack", "2ba2d60c5e8d4d1b86549d988131fe48")
        def images = connection.images()
        assert images != null
        println(images)
    }

    void testListInstances() {
        def connection = new OpenStackConnection("localhost", "admin", "stack", "2ba2d60c5e8d4d1b86549d988131fe48")
        def instances = connection.instances()
        assert instances != null
        println(instances)
    }

}
