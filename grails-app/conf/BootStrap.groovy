import node.builder.NodeType

class BootStrap {

    def init = { servletContext ->
        if(NodeType.count.is(0)){
            log.info "Creating default NodeType"
            def nodeType = new NodeType(name: "Default", description: "Default Container for nodetype less Applications")
            nodeType.save()
        }
    }
    def destroy = {
    }
}
