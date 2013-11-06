
<%@ page import="node.builder.ProjectState; node.builder.Project" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
    <g:if test="${flash.message && !layout_noflashmessage}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>
</div>

<section id="show-project" class="first">

	<table class="table">
		<tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.name.label" default="Name" /></td>

                <td valign="top" class="value">
                    ${fieldValue(bean: projectInstance, field: "name")}
                    <div class="pull-right">
                        <sec:ifAnyGranted roles="ROLE_ADMINS">
                            <g:remoteLink action="run" id="${projectInstance.id}" elementId="runProject" onFailure="runFailure(XMLHttpRequest, ${projectInstance.id})" onSuccess="runSuccess(data, ${projectInstance.id})" ><i class="icon-play-circle"></i></g:remoteLink>
                            <g:link action="edit" id="${projectInstance.id}" elementId="editProject"><i class="icon-pencil"></i></g:link>
                        </sec:ifAnyGranted>
                        <g:link action="history" id="${projectInstance.id}" elementId="historyProject"><i class="icon-info-sign"></i></g:link>
                    </div>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.description.label" default="Description" /></td>

                <td valign="top" class="value">${fieldValue(bean: projectInstance, field: "description")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.active.label" default="Active" /></td>

                <td valign="top" class="value"><g:formatBoolean boolean="active" false="No" true="Yes"/></td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.state.label" default="Status" /></td>

                <td valign="top" class="value" id="projectState">
                    <h2><i class="center ${projectInstance.state.color} ${projectInstance.state.icon}"></i></h2>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.state.label" default="Message" /></td>

                <td valign="top" class="value" id="projectMessage">
                    ${projectInstance.message}

                    <sec:ifAnyGranted roles="ROLE_ADMINS">
                        <g:if test="${projectInstance.state == ProjectState.WAITING}">
                            <hr>
                            <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskAccept' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="accept" data-task-description="${projectInstance.task.description}" data-task-name="${projectInstance.task.name}" data-project-id="${projectInstance.id}" data-project-name="${projectInstance.name}" class="icon-thumbs-up"></i></a>
                            <a  href="#completeTaskModal" data-toggle="modal" > <i id='completeTaskDecline' onclick="handleTaskComplete(this)" data-type="completeTask" data-task-decision="decline" data-task-description="${projectInstance.task.description}" data-task-name="${projectInstance.task.name}" data-project-id="${projectInstance.id}" data-project-name="${projectInstance.name}" class="icon-thumbs-down"></i></a>
                        </g:if>
                    </sec:ifAnyGranted>
                </td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.projectType.label" default="Project Type" /></td>

                <td valign="top" class="value">${projectInstance?.projectType?.encodeAsHTML()}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.location.label" default="Location" /></td>

                <td valign="top" class="value">${fieldValue(bean: projectInstance, field: "location")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.processDefinitionKey.label" default="Process Definition ID" /></td>

                <td valign="top" class="value">${fieldValue(bean: projectInstance, field: "processDefinitionKey")}</td>

            </tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="project.bpmn.label" default="Bpmn" /></td>

				<td valign="top" class="value">
                    <div id="bpmnEditor" style="width:700px;height:200px"/>
                </td>
				
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="project.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${projectInstance?.dateCreated}" /></td>
				
			</tr>

			<tr class="prop">
				<td valign="top" class="name"><g:message code="project.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${projectInstance?.lastUpdated}" /></td>
				
			</tr>
		
		</tbody>
	</table>
</section>
<g:textArea name="bpmn" value="${fieldValue(bean: projectInstance, field: "bpmn").decodeHTML()}" />
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
        $("#projectState").html('<h2><i class="black icon-cogs"></i></h2>')
        $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
        $("#projectMessage").html(data.message)
    }
    function runFailure(XMLHttpRequest, id){

        var json = JSON.parse(XMLHttpRequest.responseText)
        $("#projectState").html('<h2><i class="red icon-remove-sign"></i></h2>')
        $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
        $("#projectMessage").text(json.message)
    }


    function completeSuccess(data, id){
        $("#projectState" + data.id).html('<h2><i class="black icon-cogs"></i></h2>')
        $("#alert").html('<div class="alert alert-info">'+data.message+'</div>')
        $("#projectMessage").html(data.message)
    }
    function completeFailure(XMLHttpRequest, id){
        var json = JSON.parse(XMLHttpRequest.responseText)
        $("#projectState" + json.id).html('<h2><i class="red icon-remove-sign"></i></h2>')
        $("#alert").html('<div class="alert alert-error">'+json.message+'</div>')
        $("#projectMessage").html(json.message)
    }

    function handleTaskComplete(data){
        $("#completeTaskButton").data("project-id", $(data).data("project-id"))
        $("#completeTaskButton").data("task-decision", $(data).data("task-decision"))
        $(".modal-body #completeTaskConfirm").html( "Are you sure you want to " + $(data).data("task-decision") + " task '"+$(data).data("task-name")+"' for <i>" + $(data).data("project-name") + "</i>?")
    }

    function handleOkCompleteTask(data){
        $('#completeTaskModal').modal('hide')
        path = location.pathname.replace(/show.*/, "completeTask/"+$(data).data("project-id")+'?decision='+$(data).data("task-decision"))
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
