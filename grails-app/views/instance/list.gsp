
<%@ page import="node.builder.Instance" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'instance.label', default: 'Instance')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>

<section id="list-instance" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
				<g:sortableColumn property="name" title="${message(code: 'instance.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="privateIP" title="${message(code: 'instance.privateIp.label', default: 'Private IP')}" />
			
				<g:sortableColumn property="flavor" title="${message(code: 'instance.flavor.label', default: 'Flavor')}" />

                <g:sortableColumn property="imageId" title="${message(code: 'instance.image.label', default: 'Image')}" />

                <g:sortableColumn property="manifest" title="${message(code: 'instance.manifest.label', default: 'Manifest')}" />

                <g:sortableColumn property="progress" title="${message(code: 'instance.progress.label', default: 'Progress')}" />

                <g:sortableColumn property="status" title="${message(code: 'instance.status.label', default: 'Status')}" />
			</tr>
		</thead>
		<tbody>
		<g:each in="${instanceInstanceList}" status="i" var="instanceInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td>
                    <g:link action="show" id="${instanceInstance.id}">${fieldValue(bean: instanceInstance, field: "name")}</g:link>
                    <br>
                    <g:formatDate date="${instanceInstance.lastUpdated}" />
                </td>
			
				<td>
                    ${fieldValue(bean: instanceInstance, field: "privateIP")}
                </td>
			
				<td>
                    ${instanceInstance.flavor?.name}
                </td>

                <td>${instanceInstance.image?.name}</td>
			
				<td>${instanceInstance.deployment?.manifest?.name}</td>
			
				<td>${instanceInstance.status.toUpperCase() == 'ACTIVE' &&  instanceInstance.progress == 0? 100 : instanceInstance.progress}%</td>

                <td>${instanceInstance.status}</td>
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${instanceInstanceTotal}" />
	</div>
</section>

</body>

</html>
