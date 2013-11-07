
<%@ page import="node.builder.WorkflowState; node.builder.Workflow" %>
<!doctype html>
<html>
<head>

	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'workflow.label', default: 'Workflow')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>

<body>
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
    <g:if test="${flash.message && !layout_noflashmessage}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>
</div>

<sec:ifAnyGranted roles="ROLE_ADMINS"><g:link action="create"><i class="icon-plus-sign"></i> <b>New Workflow</b></g:link><br></sec:ifAnyGranted>
<section id="list-workflow" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>

                <g:sortableColumn property="name" title="${message(code: 'workflow.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="description" title="${message(code: 'workflow.description.label', default: 'Description')}" />

                <g:sortableColumn property="state" title="${message(code: 'workflow.state.label', default: 'Status')}" />

                <g:sortableColumn property="message" title="${message(code: 'workflow.message.label', default: 'Message')}" />

                <g:sortableColumn property="location" title="${message(code: 'workflow.location.label', default: 'Location')}" />

                <g:sortableColumn property="active" title="${message(code: 'workflow.active.label', default: 'Active')}" />

                <th><g:message code="workflow.workflowType.label" default="Workflow Type" /></th>

				<g:sortableColumn property="lastUpdated" title="${message(code: 'workflow.lastUpdated.label', default: 'Last Updated')}" />

			</tr>
		</thead>
		<tbody>
		<g:each in="${workflowInstanceList}" status="i" var="workflowInstance">
			<tr id="workflowEntry${workflowInstance.id}" class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td>
                    <g:link action="show" id="${workflowInstance.id}" elementId="workflowShow${workflowInstance.id}">${fieldValue(bean: workflowInstance, field: "name")}</g:link>
                    <hr>
                    <sec:ifAnyGranted roles="ROLE_ADMINS">
                        <g:remoteLink action="run" id="${workflowInstance.id}" elementId="runWorkflow${workflowInstance.id}" onFailure="runFailure(XMLHttpRequest, ${workflowInstance.id})" onSuccess="runSuccess(data, ${workflowInstance.id})" ><i class="icon-play-circle"></i></g:remoteLink>
                        <g:link action="edit" id="${workflowInstance.id}" elementId="editWorkflow${workflowInstance.id}"><i class="icon-pencil"></i></g:link>
                    </sec:ifAnyGranted>
                    <g:link action="history" id="${workflowInstance.id}" elementId="historyWorkflow${workflowInstance.id}"><i class="icon-info-sign"></i></g:link>
                </td>
			
				<td>${fieldValue(bean: workflowInstance, field: "description")}</td>

                <td id="workflowState${workflowInstance.id}"><h2><i class="center ${workflowInstance.state.color} ${workflowInstance.state.icon}"></i></h2></td>

                <td id="workflowMessage${workflowInstance.id}">${fieldValue(bean: workflowInstance, field: "message")}

                <sec:ifAnyGranted roles="ROLE_ADMINS">
                    <g:if test="${workflowInstance.state == WorkflowState.WAITING}">
                        <hr>
                        <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskAccept${workflowInstance.id}' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="accept" data-task-description="${workflowInstance.task.description}" data-task-name="${workflowInstance.task.name}" data-workflow-id="${workflowInstance.id}" data-workflow-name="${workflowInstance.name}" class="icon-thumbs-up"></i></a>
                        <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskDecline${workflowInstance.id}' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="decline" data-task-description="${workflowInstance.task.description}" data-task-name="${workflowInstance.task.name}" data-workflow-id="${workflowInstance.id}" data-workflow-name="${workflowInstance.name}" class="icon-thumbs-down"></i></a>
                    </g:if>
                </sec:ifAnyGranted>
                </td>

                <td>${fieldValue(bean: workflowInstance, field: "location")}</td>

                <td><g:formatBoolean boolean="${workflowInstance.active}" false="No" true="Yes"/></td>
			
				<td>${fieldValue(bean: workflowInstance, field: "workflowType")}</td>

                <td><g:formatDate date="${workflowInstance.lastUpdated}" /></td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${workflowInstanceTotal}" />
	</div>
    <g:javascript>
        function runSuccess(data, id){
            $("#workflowState" + id).html('<h2><i class="black icon-cogs"></i></h2>')
            $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
            $("#workflowMessage" + id).html(data.message)
        }
        function runFailure(XMLHttpRequest, id){

            var json = JSON.parse(XMLHttpRequest.responseText)
            $("#workflowState" + id).html('<h2><i class="red icon-remove-sign"></i></h2>')
            $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
            $("#workflowMessage" + id).text(json.message)
        }
        function completeSuccess(data, id){
            $("#workflowState" + data.id).html('<h2><i class="black icon-cogs"></i></h2>')
            $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
            $("#workflowMessage" + data.id).html(data.message)
        }
        function completeFailure(XMLHttpRequest, id){
            var json = JSON.parse(XMLHttpRequest.responseText)
            $("#workflowState" + json.id).html('<h2><i class="red icon-remove-sign"></i></h2>')
            $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
            $("#workflowMessage" + json.id).html(json.message)
        }

        function handleTaskComplete(data){
            $("#completeTaskButton").data("workflow-id", $(data).data("workflow-id"))
            $("#completeTaskButton").data("task-decision", $(data).data("task-decision"))
            $(".modal-body #completeTaskConfirm").html( "Are you sure you want to " + $(data).data("task-decision") + " task '"+$(data).data("task-name")+"' for <i>" + $(data).data("workflow-name") + "</i>?")
        }

        function handleOkCompleteTask(data){
            $('#completeTaskModal').modal('hide')
            path = location.pathname.replace(/list.*/, "completeTask/"+$(data).data("workflow-id")+'?decision='+$(data).data("task-decision"))
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
