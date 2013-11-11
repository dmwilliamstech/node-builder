
<%@ page import="node.builder.SubscriptionVariable" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
	
<section id="list-subscriptionVariable" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'subscriptionVariable.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'subscriptionVariable.lastUpdated.label', default: 'Last Updated')}" />
			
				<g:sortableColumn property="name" title="${message(code: 'subscriptionVariable.name.label', default: 'Name')}" />
			
				<th><g:message code="subscriptionVariable.subscription.label" default="Subscription" /></th>
			
				<g:sortableColumn property="value" title="${message(code: 'subscriptionVariable.value.label', default: 'Value')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${subscriptionVariableInstanceList}" status="i" var="subscriptionVariableInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${subscriptionVariableInstance.id}">${fieldValue(bean: subscriptionVariableInstance, field: "dateCreated")}</g:link></td>
			
				<td><g:formatDate date="${subscriptionVariableInstance.lastUpdated}" /></td>
			
				<td>${fieldValue(bean: subscriptionVariableInstance, field: "name")}</td>
			
				<td>${fieldValue(bean: subscriptionVariableInstance, field: "subscription")}</td>
			
				<td>${fieldValue(bean: subscriptionVariableInstance, field: "value")}</td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${subscriptionVariableInstanceTotal}" />
	</div>
</section>

</body>

</html>
