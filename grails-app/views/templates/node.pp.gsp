group { "puppet": ensure => "present", }

Exec { path => "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin", }


node '${manifest.instanceName}' {
    <g:each in="${manifest.nodes}" var="node">
        <g:each in="${node.getValue().configurations}" var="configuration">
    $${configuration.name} = "${configuration.value}"
        </g:each>
    class { "${node.getValue().name}": }
    </g:each>

    <g:each in="${manifest.applications}" var="application">
        <g:each in="${application.getValue().configurations}" var="configuration">
    $${configuration.name} = "${configuration.value}"
        </g:each>
    class { "${application.getValue().name}": }
    </g:each>
}