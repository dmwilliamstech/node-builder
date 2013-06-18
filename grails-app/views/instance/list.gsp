
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
			
				<g:sortableColumn property="flavorId" title="${message(code: 'instance.flavor.label', default: 'Flavor ID')}" />

                <g:sortableColumn property="imageId" title="${message(code: 'instance.image.label', default: 'Image ID')}" />
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'instance.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'instance.lastUpdated.label', default: 'Last Updated')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${instanceInstanceList}" status="i" var="instanceInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${instanceInstance.id}">${fieldValue(bean: instanceInstance, field: "name")}</g:link></td>
			
				<td>${fieldValue(bean: instanceInstance, field: "privateIP")}</td>
			
				<td>${fieldValue(bean: instanceInstance, field: "flavorId")}</td>

                <td>${instanceInstance.image.imageId}</td>
			
				<td><g:formatDate date="${instanceInstance.dateCreated}" /></td>
			
				<td><g:formatDate date="${instanceInstance.lastUpdated}" /></td>
			
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
