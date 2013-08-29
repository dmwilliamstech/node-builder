
<%@ page import="node.builder.Project" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'project.label', default: 'Project')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>

<section id="show-project" class="first">

	<table class="table">
		<tbody>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.name.label" default="Name" /></td>

                <td valign="top" class="value">${fieldValue(bean: projectInstance, field: "name")}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.description.label" default="Description" /></td>

                <td valign="top" class="value">${fieldValue(bean: projectInstance, field: "description")}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.active.label" default="Active" /></td>

                <td valign="top" class="value">${projectInstance.active? "Yes": "No"}</td>

            </tr>

            <tr class="prop">
                <td valign="top" class="name"><g:message code="project.projectType.label" default="Project Type" /></td>

                <td valign="top" class="value"><g:link controller="projectType" action="show" id="${projectInstance?.projectType?.id}">${projectInstance?.projectType?.encodeAsHTML()}</g:link></td>

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
<g:textArea name="bpmn" value="${fieldValue(bean: projectInstance, field: "bpmn")}" />
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
</g:javascript>

</body>

</html>
