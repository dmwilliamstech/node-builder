
<%@ page import="grails.converters.JSON; node.builder.Instance" %>

<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'instance.label', default: 'Manifest')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
<div id="alert">
    <g:if test="${flash.message && !layout_noflashmessage}">
        <div class="alert alert-error">${flash.message}</div>
    </g:if>
</div>
</section>
<p>
<a href="show/new" title="Add a Manifest"><strong><i class="icon-plus-sign"></i> New Manifest</strong></a>
</p>
<section id="list-manifest" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
                <g:sortableColumn property="name" title="${message(code: 'manifest.name.label', default: 'Name')}" />

                <th><g:message code="manifest.manifest.label" default="Manifest" /></th>

                <th><g:message code="manifest.deployments.label" default="Deployments" /></th>

			</tr>
		</thead>
		<tbody>
		<g:each in="${manifests}" status="i" var="manifest">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>
                    <h4>${manifest.name}</h4>
                    <g:formatDate date="${manifest.lastUpdated}" />
                    <hr>
                    <a href="show/${manifest.id}"><i class="icon-pencil"></i></a>
                    <a href="deploy/${manifest.id}"> <i class="icon-upload"></i></a>
                    <a href="#deleteModal" data-toggle="modal"> <i data-type="manifest" data-manifest-id="${manifest.id}" data-manifest-name="${manifest.name}" class="icon-remove-sign"></i></a>
                    <a class="" data-toggle="collapse" data-target="#viewdetails${manifest.id}"><i class="icon-info-sign"></i></a>
                    <div class="collapse-group">
                        <p id="viewdetails${manifest.id}" class="collapse">${manifest.description}</p>

                    </div>
                </td>

				<td>
                    <pre class="pre-scrollable">${(manifest.manifest as JSON).toString(true)}</pre>
                </td>
                <td class="span4">
                <g:each in="${manifest.deployments}" var="deployment">
                    <div  id="deployment${deployment.id}">
                    Deployment ${manifest.name} ${deployment.id}
                    <a href="#deleteDeploymentModal" data-toggle="modal" > <i data-type="deployment" data-manifest-id="${manifest.id}" data-manifest-name="${manifest.name}" data-deployment-id="${deployment.id}" class="icon-remove-sign"></i></a>
                    <ul>
                    <g:each in="${deployment.instances}" var="instance">
                        <li>${instance.name} (${instance.instanceId})</li>
                    </g:each>
                    </ul>
                    </div>
                </g:each>
                </td>

			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${manifestInstanceTotal}" />
	</div>
</section>

<div id="deleteModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="deleteModalLabel">Delete Manifest</h3>
    </div>
    <div class="modal-body">
        <div id="deleteManifestConfirm">Are you sure you want to delete?</div>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" data-target="#deleteModal" >Cancel</button>
        <button class="btn btn-primary" onclick="handleDeleteManifest(this)" >Delete</button>
    </div>
</div>

<div id="deleteDeploymentModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="deleteDeploymentModalLabel">Delete Deployment</h3>
    </div>
    <div class="modal-body">
        <div id="deleteDeploymentConfirm">Are you sure you want to delete?</div>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" data-target="#deleteDeploymentModal" >Cancel</button>
        <button id="deleteDeploymentButton" class="btn btn-primary" onclick="handleUndeploy(this)" >Delete</button>
    </div>
</div>

<script type="text/javascript" src="/node-builder/static/js/manifest.js" ></script>
</body>

</html>
