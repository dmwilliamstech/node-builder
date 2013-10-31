
<%@ page import="node.builder.ProjectState; node.builder.Project" %>
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

                <td id="projectMessage${projectInstance.id}">${fieldValue(bean: projectInstance, field: "message")}
                    <hr>
                <sec:ifAnyGranted roles="ROLE_ADMINS">
                    <g:if test="${projectInstance.state == ProjectState.WAITING}">
                        <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskAccept${projectInstance.id}' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="accept" data-task-description="${projectInstance.task.description}" data-task-name="${projectInstance.task.name}" data-project-id="${projectInstance.id}" data-project-name="${projectInstance.name}" class="icon-thumbs-up"></i></a>
                        <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskDecline${projectInstance.id}' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="decline" data-task-description="${projectInstance.task.description}" data-task-name="${projectInstance.task.name}" data-project-id="${projectInstance.id}" data-project-name="${projectInstance.name}" class="icon-thumbs-down"></i></a>
                    </g:if>
                </sec:ifAnyGranted>
                </td>

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
            $("#projectState" + id).html('<h2><i class="black icon-cogs"></i></h2>')
            $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
            $("#projectMessage" + id).html(data.message)
        }
        function runFailure(XMLHttpRequest, id){

            var json = JSON.parse(XMLHttpRequest.responseText)
            $("#projectState" + id).html('<h2><i class="red icon-remove-sign"></i></h2>')
            $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
            $("#projectMessage" + id).text(json.message)
        }
        function completeSuccess(data, id){
            $("#projectState" + data.id).html('<h2><i class="black icon-cogs"></i></h2>')
            $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
            $("#projectMessage" + data.id).html(data.message)
        }
        function completeFailure(XMLHttpRequest, id){
            var json = JSON.parse(XMLHttpRequest.responseText)
            $("#projectState" + json.id).html('<h2><i class="red icon-remove-sign"></i></h2>')
            $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
            $("#projectMessage" + json.id).html(json.message)
        }

        function handleTaskComplete(data){
            $("#completeTaskButton").data("project-id", $(data).data("project-id"))
            $("#completeTaskButton").data("task-decision", $(data).data("task-decision"))
            $(".modal-body #completeTaskConfirm").html( "Are you sure you want to " + $(data).data("task-decision") + " task '"+$(data).data("task-name")+"' for <i>" + $(data).data("project-name") + "</i>?")
        }

        function handleOkCompleteTask(data){
            $('#completeTaskModal').modal('hide')
            path = location.pathname.replace(/list.*/, "completeTask/"+$(data).data("project-id")+'?decision='+$(data).data("task-decision"))
            $.ajax(path, {
                contentType : 'application/json',
                type : 'POST',
                success: completeSuccess,
                failure: completeFailure
            });
        }
    </g:javascript>
</section>


<div id="completeTaskModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="completeTaskLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="completeTaskModalLabel">Complete Task</h3>
    </div>
    <div class="modal-body">
        <div id="completeTaskConfirm">Are you sure you want to complete this task?</div>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal" data-target="#completeTaskModal" >Cancel</button>
        <button id="completeTaskButton" class="btn btn-primary" onclick="handleOkCompleteTask(this)" >OK</button>
    </div>
</div>

</body>

</html>
