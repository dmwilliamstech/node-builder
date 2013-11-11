
<%@ page import="node.builder.Organization" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'organization.label', default: 'Organization')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
	
<section id="list-organization" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="name" title="${message(code: 'organization.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'organization.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="description" title="${message(code: 'organization.description.label', default: 'Description')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'organization.lastUpdated.label', default: 'Last Updated')}" />
			
				<th><g:message code="organization.subscriptionLevel.label" default="Subscription Level" /></th>
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${organizationInstanceList}" status="i" var="organizationInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${organizationInstance.id}">${fieldValue(bean: organizationInstance, field: "name")}</g:link></td>
			
				<td><g:formatDate date="${organizationInstance.dateCreated}" /></td>
			
				<td>${fieldValue(bean: organizationInstance, field: "description")}</td>
			
				<td><g:formatDate date="${organizationInstance.lastUpdated}" /></td>
			
				<td>${fieldValue(bean: organizationInstance, field: "subscriptionLevel")}</td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${organizationInstanceTotal}" />
	</div>
</section>

</body>

</html>
