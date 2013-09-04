<%@ page import="node.builder.Project" %>
            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'projectType', 'error')} required">
                <label for="projectType" class="control-label"><g:message code="project.projectType.label" default="Project Type" /><span class="required-indicator">*</span></label>
                <div class="controls">
                    <g:select id="projectType" name="projectType.id" from="${node.builder.ProjectType.list()}" optionKey="id" required="" value="${projectInstance?.projectType?.id}" class="many-to-one"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'projectType', 'error')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'name', 'error')} ">
                <label for="name" class="control-label"><g:message code="project.name.label" default="Name" /></label>
                <div class="controls">
                    <g:textField name="name" value="${projectInstance?.name}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'name', 'error')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'description', 'error')} ">
                <label for="description" class="control-label"><g:message code="project.description.label" default="Description" /></label>
                <div class="controls">
                    <g:textField name="description" value="${projectInstance?.description}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'description', 'error')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'location', 'error')} ">
                <label for="location" class="control-label"><g:message code="project.location.label" default="Location" /></label>
                <div class="controls">
                    <g:textField name="location" value="${projectInstance?.location}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'location', 'error')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'processDefinitionKey', 'error')} ">
                <label for="processDefinitionKey" class="control-label"><g:message code="project.processDefinitionKey.label" default="Process Definition ID" /></label>
                <div class="controls">
                    <g:textField name="processDefinitionKey" value="${projectInstance?.processDefinitionKey}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'processDefinitionKey', 'error')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'bpmn', 'error')} ">
				<label for="bpmnEditor" class="control-label"><g:message code="project.bpmn.label" default="Bpmn" /></label>
				<div class="controls">
                    <div id="bpmnEditor" style="width:700px;height:200px"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'bpmn', 'error')}</span>
				</div>
			</div>
            <g:textArea rows="20" class="span8" name="bpmn" value="${projectInstance?.bpmn}"/>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'active', 'error')} ">
                <label for="active" class="control-label"><g:message code="project.active.label" default="Active" /></label>
                <div class="controls">
                    <g:checkBox rows="20" class="span8" name="active" value="${projectInstance?.active}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'active', 'error')}</span>
                </div>
            </div>

            <g:javascript library="ace" />
            <g:javascript library="mode_xml" />
            <g:javascript>
                var editor = ace.edit("bpmnEditor");
                var Mode = ace.require('ace/mode/xml').Mode;
                editor.getSession().setMode(new Mode());
                var textarea = $('textarea[name="bpmn"]').hide();

                editor.getSession().setValue(textarea.val());
                editor.getSession().on('change', function(){
                    textarea.val(editor.getSession().getValue());
                });
            </g:javascript>