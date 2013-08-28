
<%@ page import="node.builder.Project" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
	
<section id="list-project" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>

                <g:sortableColumn property="name" title="${message(code: 'project.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="description" title="${message(code: 'project.description.label', default: 'Description')}" />

                <g:sortableColumn property="location" title="${message(code: 'project.location.label', default: 'Location')}" />

                <g:sortableColumn property="active" title="${message(code: 'project.active.label', default: 'Active')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'project.lastUpdated.label', default: 'Last Updated')}" />
			
				<th><g:message code="project.projectType.label" default="Project Type" /></th>
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${projectInstanceList}" status="i" var="projectInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${projectInstance.id}">${fieldValue(bean: projectInstance, field: "name")}</g:link> <sec:ifAnyGranted roles="ROLE_ADMIN"><g:link action="edit" id="${projectInstance.id}"><i class="icon-pencil"/></g:link></sec:ifAnyGranted></td>
			
				<td>${fieldValue(bean: projectInstance, field: "description")}</td>

                <td>${fieldValue(bean: projectInstance, field: "location")}</td>

                <td><g:formatBoolean boolean="${projectInstance.active}" false="No" true="Yes"/></td>

				<td><g:formatDate date="${projectInstance.lastUpdated}" /></td>
			
				<td>${fieldValue(bean: projectInstance, field: "projectType")}</td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${projectInstanceTotal}" />
	</div>
</section>

</body>

</html>
