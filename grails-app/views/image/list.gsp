
<%@ page import="node.builder.Image" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'image.label', default: 'Image')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>

<section id="list-image" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="name" title="${message(code: 'image.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'image.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'image.lastUpdated.label', default: 'Last Updated')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${imageInstanceList}" status="i" var="imageInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${imageInstance.id}">${fieldValue(bean: imageInstance, field: "name")}</g:link></td>
			
				<td><g:formatDate date="${imageInstance.dateCreated}" /></td>
			
				<td><g:formatDate date="${imageInstance.lastUpdated}" /></td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${imageInstanceTotal}" />
	</div>
</section>

</body>

</html>
