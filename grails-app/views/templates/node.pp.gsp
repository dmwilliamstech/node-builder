
node '${manifest.instanceName}.novalocal' {
    %{--<g:each in="${manifest.nodes.sort()}" var="node">--}%
        %{--<g:each in="${node.getValue().configurations}" var="configuration">--}%
    %{--$${configuration.name} = "${configuration.value}"--}%
        %{--</g:each>--}%
    %{--class { "${node.getValue().name}": }--}%
    %{--</g:each>--}%

    <g:each in="${manifest.applications.sort()}" var="application">

    class { "${application.getValue().name}":
        <g:each in="${application.getValue().configurations}" var="configuration">
            ${configuration.name} => "${configuration.value}",
        </g:each>
    } ->
    </g:each>
}