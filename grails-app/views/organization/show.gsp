
<%@ page import="node.builder.Organization" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'organization.label', default: 'Organization')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-organization" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="organization.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: organizationInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="organization.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${organizationInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="organization.description.label" default="Description" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: organizationInstance, field: "description")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="organization.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${organizationInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="organization.subscriptionLevel.label" default="Subscription Level" /></td>
				
				<td valign="top" class="value"><g:link controller="subscriptionLevel" action="show" id="${organizationInstance?.subscriptionLevel?.id}">${organizationInstance?.subscriptionLevel?.encodeAsHTML()}</g:link></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="organization.subscriptions.label" default="Subscriptions" /></td>
				
				<td valign="top" style="text-align: left;" class="value">
					<ul>
					<g:each in="${organizationInstance.subscriptions}" var="s">
						<li><g:link controller="subscription" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
					</g:each>
					</ul>
				</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
