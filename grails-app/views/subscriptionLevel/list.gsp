
<%@ page import="node.builder.SubscriptionLevel" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subscriptionLevel.label', default: 'SubscriptionLevel')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
	
<section id="list-subscriptionLevel" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="name" title="${message(code: 'subscriptionLevel.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="description" title="${message(code: 'subscriptionLevel.description.label', default: 'Description')}" />
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'subscriptionLevel.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'subscriptionLevel.lastUpdated.label', default: 'Last Updated')}" />
			
				<g:sortableColumn property="subscriptionCount" title="${message(code: 'subscriptionLevel.subscriptionCount.label', default: 'Subscription Count')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${subscriptionLevelInstanceList}" status="i" var="subscriptionLevelInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${subscriptionLevelInstance.id}">${fieldValue(bean: subscriptionLevelInstance, field: "name")}</g:link></td>
			
				<td>${fieldValue(bean: subscriptionLevelInstance, field: "description")}</td>
			
				<td><g:formatDate date="${subscriptionLevelInstance.dateCreated}" /></td>
			
				<td><g:formatDate date="${subscriptionLevelInstance.lastUpdated}" /></td>
			
				<td>${fieldValue(bean: subscriptionLevelInstance, field: "subscriptionCount")}</td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${subscriptionLevelInstanceTotal}" />
	</div>
</section>

</body>

</html>
