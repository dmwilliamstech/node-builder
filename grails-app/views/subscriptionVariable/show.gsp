
<%@ page import="node.builder.SubscriptionVariable" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-subscriptionVariable" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionVariable.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${subscriptionVariableInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionVariable.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${subscriptionVariableInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionVariable.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: subscriptionVariableInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionVariable.subscription.label" default="Subscription" /></td>
				
				<td valign="top" class="value"><g:link controller="subscription" action="show" id="${subscriptionVariableInstance?.subscription?.id}">${subscriptionVariableInstance?.subscription?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionVariable.value.label" default="Value" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: subscriptionVariableInstance, field: "value")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
