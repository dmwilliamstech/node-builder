node '${manifest.name.replaceAll(/\s/, '-')}.novalocal' {
    <% manifest.nodes.each { node ->  %>
        <% node.getValue().configurations.each { configuration -> %>
    \$${configuration.name} = "${configuration.value}"
        <% } %>
    <% } %>
    <% manifest.applications.sort({it.priority})each { application ->  %>
    class { "${application.getValue().name}":
        <% application.getValue().configurations.each { configuration -> %>
            ${configuration.name} => ${ service.processConfigurationValue(configuration.value.json) },
        <% } %>
    } ->
    <% } %>
}