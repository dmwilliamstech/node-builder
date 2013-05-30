package node.builder

import grails.converters.JSON

class NodeController {

    def types() {
        render NodeType.all as JSON
    }



}
