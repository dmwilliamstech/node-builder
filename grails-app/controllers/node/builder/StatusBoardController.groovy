package node.builder

/**
 * StatusBoardController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class StatusBoardController {
    def instanceService

    def instance () {
        instanceService.loadInstances(OpenStackConnection.connection)
        def instances = Instance.getAll()

        render(template: "instanceTable", model: [instances:instances])
    }

}
