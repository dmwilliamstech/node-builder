
<%@ page import="node.builder.Instance" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'instance.label', default: 'Instance')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-instance" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="instance.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: instanceInstance, field: "name")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="instance.ip.label" default="Ip" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: instanceInstance, field: "ip")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="instance.address.label" default="Address" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: instanceInstance, field: "address")}</td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="instance.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${instanceInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="instance.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${instanceInstance?.lastUpdated}" /></td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
