
<%@ page import="node.builder.Subscription" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'subscription.label', default: 'Subscription')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
    <sec:ifAnyGranted roles="ROLE_ADMINS"><g:link action="create"><i class="icon-plus-sign"></i> <b>Subscribe to a Project</b></g:link><br></sec:ifAnyGranted>
    <sec:ifAllGranted roles="ROLE_USERS">
        <sec:ifNotGranted roles="ROLE_NBADMINS">
            <g:render template="userList"/>
        </sec:ifNotGranted>
    </sec:ifAllGranted>
    <sec:ifAllGranted roles="ROLE_NBADMINS">
        <g:render template="adminList"/>
    </sec:ifAllGranted>
</body>

</html>
