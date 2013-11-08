
<%@ page import="node.builder.WorkflowState; node.builder.Workflow" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'workflow.label', default: 'Workflow')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
    <g:if test="${flash.message && !layout_noflashmessage}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>
</div>

<section id="show-workflow" class="first">

	<table class="table">
		<tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.name.label" default="Name" /></td>

                <td valign="top" class="value">
                    ${fieldValue(bean: workflowInstance, field: "name")}
                    <div class="pull-right">
                        <sec:ifAnyGranted roles="ROLE_ADMINS">
                            <g:remoteLink action="run" id="${workflowInstance.id}" elementId="runWorkflow" onFailure="runFailure(XMLHttpRequest, ${workflowInstance.id})" onSuccess="runSuccess(data, ${workflowInstance.id})" ><i class="icon-play-circle"></i></g:remoteLink>
                            <g:link action="edit" id="${workflowInstance.id}" elementId="editWorkflow"><i class="icon-pencil"></i></g:link>
                        </sec:ifAnyGranted>
                        <g:link action="history" id="${workflowInstance.id}" elementId="historyWorkflow"><i class="icon-info-sign"></i></g:link>
                    </div>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.description.label" default="Description" /></td>

                <td valign="top" class="value">${fieldValue(bean: workflowInstance, field: "description")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.information.label" default="Information" /></td>

                <td valign="top" class="value">
                    <ul class="nav nav-tabs nav-stacked">
                        <li class="">
                            Active <div class="pull-right label ${workflowInstance.active? "label-success":"label-default"}"><g:formatBoolean boolean="${workflowInstance.active}" false="No" true="Yes"/></div>
                        </li>
                        <li>
                            Subscribable <div class="pull-right label ${workflowInstance.subscribable? "label-success":"label-default"}"><g:formatBoolean boolean="${workflowInstance.subscribable}" false="No" true="Yes"/></div>
                        </li>
                        <li>
                            Tags
                            <ul class="pull-right nav nav-tabs nav-stacked">
                                <g:each in="${workflowInstance.tags}" var="tag">
                                    <li>
                                        <div class="label label-info">${tag.name}</div>
                                    </li>
                                </g:each>
                            </ul>
                        </li>
                    </ul>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.state.label" default="Status" /></td>

                <td valign="top" class="value" id="workflowState">
                    <h2><i class="center ${workflowInstance.state.color} ${workflowInstance.state.icon}"></i></h2>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.state.label" default="Message" /></td>

                <td valign="top" class="value" id="workflowMessage">
                    ${workflowInstance.message}

                    <sec:ifAnyGranted roles="ROLE_ADMINS">
                        <g:if test="${workflowInstance.state == WorkflowState.WAITING}">
                            <hr>
                            <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskAccept' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="accept" data-task-description="${workflowInstance.task.description}" data-task-name="${workflowInstance.task.name}" data-workflow-id="${workflowInstance.id}" data-workflow-name="${workflowInstance.name}" class="icon-thumbs-up"></i></a>
                            <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskDecline' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="decline" data-task-description="${workflowInstance.task.description}" data-task-name="${workflowInstance.task.name}" data-workflow-id="${workflowInstance.id}" data-workflow-name="${workflowInstance.name}" class="icon-thumbs-down"></i></a>
                        </g:if>
                    </sec:ifAnyGranted>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.workflowType.label" default="Workflow Type" /></td>

                <td valign="top" class="value">${workflowInstance?.workflowType?.encodeAsHTML()}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.location.label" default="Location" /></td>

                <td valign="top" class="value">${fieldValue(bean: workflowInstance, field: "location")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="workflow.processDefinitionKey.label" default="Process Definition ID" /></td>

                <td valign="top" class="value">${fieldValue(bean: workflowInstance, field: "processDefinitionKey")}</td>

            </tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="workflow.bpmn.label" default="Bpmn" /></td>

				<td valign="top" class="value">
                    <div id="bpmnEditor" style="width:700px;height:200px"/>
                </td>
				
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="workflow.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${workflowInstance?.dateCreated}" /></td>
				
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="workflow.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${workflowInstance?.lastUpdated}" /></td>
				
			</tr>
		
		</tbody>
	</table>
</section>
<g:textArea name="bpmn" value="${fieldValue(bean: workflowInstance, field: "bpmn").decodeHTML()}" />
<g:javascript library="ace" />
<g:javascript library="mode_xml" />
<g:javascript>
    var editor = ace.edit("bpmnEditor");
    var Mode = ace.require('ace/mode/xml').Mode;
    editor.getSession().setMode(new Mode());
    var textarea = $('textarea[name="bpmn"]').hide();
    editor.setReadOnly(true);
    editor.getSession().setValue(textarea.val());
    editor.getSession().on('change', function(){
        textarea.val(editor.getSession().getValue());
    });



    function runSuccess(data, id){
        $("#workflowState").html('<h2><i class="black icon-cogs"></i></h2>')
        $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
        $("#workflowMessage").html(data.message)
    }
    function runFailure(XMLHttpRequest, id){

        var json = JSON.parse(XMLHttpRequest.responseText)
        $("#workflowState").html('<h2><i class="red icon-remove-sign"></i></h2>')
        $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
        $("#workflowMessage").text(json.message)
    }


    function completeSuccess(data, id){
        $("#workflowState" + data.id).html('<h2><i class="black icon-cogs"></i></h2>')
        $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
        $("#workflowMessage").html(data.message)
    }
    function completeFailure(XMLHttpRequest, id){
        var json = JSON.parse(XMLHttpRequest.responseText)
        $("#workflowState" + json.id).html('<h2><i class="red icon-remove-sign"></i></h2>')
        $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
        $("#workflowMessage").html(json.message)
    }

    function handleTaskComplete(data){
        $("#completeTaskButton").data("workflow-id", $(data).data("workflow-id"))
        $("#completeTaskButton").data("task-decision", $(data).data("task-decision"))
        $(".modal-body #completeTaskConfirm").html( "Are you sure you want to " + $(data).data("task-decision") + " task '"+$(data).data("task-name")+"' for <i>" + $(data).data("workflow-name") + "</i>?")
    }

    function handleOkCompleteTask(data){
        $('#completeTaskModal').modal('hide')
        path = location.pathname.replace(/show.*/, "completeTask/"+$(data).data("workflow-id")+'?decision='+$(data).data("task-decision"))
        $.ajax(path, {
            contentType : 'application/json',
            type : 'POST',
            success: completeSuccess,
            failure: completeFailure
        });
    }
</g:javascript>

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
