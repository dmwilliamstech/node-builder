
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

</section>
<p>
<a href="${request.contextPath}" title="Add a Manifest"><strong><i class="icon-plus-sign"></i> New Manifest</strong></a>
</p>
<section id="list-manifest" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
                <g:sortableColumn property="name" title="${message(code: 'manifest.name.label', default: 'Name')}" />

				<g:sortableColumn property="manifest" title="${message(code: 'manifest.manifest.label', default: 'Manifest')}" />

                <g:sortableColumn property="instances" title="${message(code: 'manifest.instances.label', default: 'Instances')}" />

				<g:sortableColumn property="dateCreated" title="${message(code: 'manifest.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'manifest.lastUpdated.label', default: 'Last Updated')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${manifests}" status="i" var="manifest">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><h4>${manifest.name}</h4></td>

				<td><pre>${(manifest.manifest as JSON).toString(true)}</pre></td>

                <td>
                    <ul>
                    <g:each in="${manifest.instances}" var="instance">
                        <li>${instance.name} (${instance.instanceId})</li>
                    </g:each>
                    </ul>
                </td>

				<td><g:formatDate date="${manifest.dateCreated}" /></td>
			
				<td><g:formatDate date="${manifest.lastUpdated}" /></td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${manifestInstanceTotal}" />
	</div>
</section>

</body>

</html>
