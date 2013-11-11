
<%@ page import="node.builder.Subscription" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-subscription" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscription.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${subscriptionInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscription.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${subscriptionInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscription.organization.label" default="Organization" /></td>
				
				<td valign="top" class="value"><g:link controller="organization" action="show" id="${subscriptionInstance?.organization?.id}">${subscriptionInstance?.organization?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscription.variables.label" default="Variables" /></td>
				
				<td valign="top" style="text-align: left;" class="value">
					<ul>
					<g:each in="${subscriptionInstance.variables}" var="v">
						<li><g:link controller="subscriptionVariable" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></li>
					</g:each>
					</ul>
				</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscription.workflowTag.label" default="Workflow Tag" /></td>
				
				<td valign="top" class="value"><g:link controller="workflowTag" action="show" id="${subscriptionInstance?.workflowTag?.id}">${subscriptionInstance?.workflowTag?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
