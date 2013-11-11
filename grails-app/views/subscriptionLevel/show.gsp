
<%@ page import="node.builder.SubscriptionLevel" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subscriptionLevel.label', default: 'Subscription Level')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-subscriptionLevel" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionLevel.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: subscriptionLevelInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionLevel.description.label" default="Description" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: subscriptionLevelInstance, field: "description")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionLevel.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${subscriptionLevelInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionLevel.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${subscriptionLevelInstance?.lastUpdated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionLevel.organizations.label" default="Organizations" /></td>
				
				<td valign="top" style="text-align: left;" class="value">
					<ul>
					<g:each in="${subscriptionLevelInstance.organizations}" var="o">
						<li><g:link controller="organization" action="show" id="${o.id}">${o?.encodeAsHTML()}</g:link></li>
					</g:each>
					</ul>
				</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="subscriptionLevel.subscriptionCount.label" default="Subscription Count" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: subscriptionLevelInstance, field: "subscriptionCount")}</td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
