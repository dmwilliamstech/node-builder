
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
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
    <g:if test="${flash.message && !layout_noflashmessage}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>
</div>

<sec:ifAnyGranted roles="ROLE_ADMINS"><g:link action="create"><i class="icon-plus-sign"></i> <b>New Project</b></g:link><br></sec:ifAnyGranted>
<section id="list-project" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>

                <g:sortableColumn property="name" title="${message(code: 'project.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="description" title="${message(code: 'project.description.label', default: 'Description')}" />

                <g:sortableColumn property="state" title="${message(code: 'project.state.label', default: 'Status')}" />

                <g:sortableColumn property="message" title="${message(code: 'project.message.label', default: 'Message')}" />

                <g:sortableColumn property="location" title="${message(code: 'project.location.label', default: 'Location')}" />

                <g:sortableColumn property="active" title="${message(code: 'project.active.label', default: 'Active')}" />

                <th><g:message code="project.projectType.label" default="Project Type" /></th>

				<g:sortableColumn property="lastUpdated" title="${message(code: 'project.lastUpdated.label', default: 'Last Updated')}" />

			</tr>
		</thead>
		<tbody>
		<g:each in="${projectInstanceList}" status="i" var="projectInstance">
			<tr id="projectEntry${projectInstance.id}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td>
                    <g:link action="show" id="${projectInstance.id}" elementId="projectShow${projectInstance.id}">${fieldValue(bean: projectInstance, field: "name")}</g:link>
                    <hr>
                    <sec:ifAnyGranted roles="ROLE_ADMINS">
                        <g:remoteLink action="run" id="${projectInstance.id}" elementId="runProject${projectInstance.id}" onFailure="runFailure(XMLHttpRequest, ${projectInstance.id})" onSuccess="runSuccess(data, ${projectInstance.id})" ><i class="icon-play-circle"></i></g:remoteLink>
                        <g:link action="edit" id="${projectInstance.id}" elementId="editProject${projectInstance.id}"><i class="icon-pencil"></i></g:link>
                    </sec:ifAnyGranted>
                    <g:link action="history" id="${projectInstance.id}" elementId="historyProject${projectInstance.id}"><i class="icon-info-sign"></i></g:link>
                </td>
			
				<td>${fieldValue(bean: projectInstance, field: "description")}</td>

                <td id="projectState${projectInstance.id}"><h2><i class="center ${projectInstance.state.color} ${projectInstance.state.icon}"></i></h2></td>

                <td id="projectMessage${projectInstance.id}">${fieldValue(bean: projectInstance, field: "message")}</td>

                <td>${fieldValue(bean: projectInstance, field: "location")}</td>

                <td><g:formatBoolean boolean="${projectInstance.active}" false="No" true="Yes"/></td>
			
				<td>${fieldValue(bean: projectInstance, field: "projectType")}</td>

                <td><g:formatDate date="${projectInstance.lastUpdated}" /></td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${projectInstanceTotal}" />
	</div>
    <g:javascript>
        function runSuccess(data, id){
            $("#projectState" + id).html('<h2><i class="icon-refresh"></i></h2>')
            $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
        }
        function runFailure(XMLHttpRequest, id){

            var json = JSON.parse(XMLHttpRequest.responseText)
            $("#projectState" + id).html("ERROR")
            $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
        }
    </g:javascript>
</section>

</body>

</html>
