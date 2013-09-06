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
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'name', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'description', 'error')} ">
                <label for="description" class="control-label"><g:message code="project.description.label" default="Description" /></label>
                <div class="controls">
                    <g:textField name="description" value="${projectInstance?.description}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'description', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'location', 'error')} ">
                <label for="location" class="control-label"><g:message code="project.location.label" default="Location" /></label>
                <div class="controls">
                    <g:textField name="location" value="${projectInstance?.location}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'location', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'processDefinitionKey', 'error')} ">
                <label for="processDefinitionKey" class="control-label"><g:message code="project.processDefinitionKey.label" default="Process Definition ID" /></label>
                <div class="controls">
                    <select name="processDefinitionKey" id="processDefinitionKey" value="" >

                    </select>
                    %{--<g:textField name="processDefinitionKey" value="${projectInstance?.processDefinitionKey}"/>--}%
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'processDefinitionKey', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'bpmn', 'error')} ">
				<label for="bpmnEditor" class="control-label"><g:message code="project.bpmn.label" default="Bpmn" /></label>
				<div class="controls">
                    <div id="bpmnEditor" style="width:700px;height:200px"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'bpmn', '<i class="icon-exclamation-sign"></i>')}</span>
				</div>
			</div>
            <g:textArea id="bpmnTextArea" rows="20" class="span8" name="bpmn" value="${projectInstance?.bpmn}"/>

            <div class="control-group fieldcontain ${hasErrors(bean: projectInstance, field: 'active', 'error')} ">
                <label for="active" class="control-label"><g:message code="project.active.label" default="Active" /></label>
                <div class="controls">
                    <g:checkBox rows="20" class="span8" name="active" value="${projectInstance?.active}"/>
                    <span class="help-inline">${hasErrors(bean: projectInstance, field: 'active', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <g:javascript library="xml2json" />
            <g:javascript library="ace" />
            <g:javascript library="mode_xml" />
            <g:javascript>
                $(document).ready(function(){
                    var x2js = new X2JS();
                    var editor = ace.edit("bpmnEditor");
                    var Mode = ace.require('ace/mode/xml').Mode;
                    var processIds = {}
                    editor.getSession().setMode(new Mode());
                    var textarea = $('#bpmnTextArea').hide();

                    parseProcessIds(textarea.val(), "${projectInstance.processDefinitionKey}")

                    editor.getSession().setValue(textarea.val());
                    editor.getSession().on('change', function(){
                        $('#processDefinitionKey')
                            .find('option')
                            .remove()
                        var value =  editor.getSession().getValue()
                        parseProcessIds(value, "")
                        textarea.val(value);
                    });

                    function parseProcessIds(definition, current){
                        if(definition.length < 1)
                            return

                        var json = x2js.xml_str2json( definition )
                        if(json.definitions != null)
                            if(json.definitions.process != null && json.definitions.process instanceof Array){
                                $.each(json.definitions.process, function(index, process){
                                    processIds[ process._id ] = process._id
                                });
                            }else if(json.definitions.process != null){
                                processIds[ json.definitions.process._id ] = json.definitions.process._id
                            }

                        createProcessIdOptions(processIds, current)
                    }

                    function createProcessIdOptions(processIds, current){
                        $.each(processIds, function(key, value) {
                            if($("#processDefinitionKey option[value='"+value+"']").length == 0){
                                $('#processDefinitionKey')
                                    .append($("<option "+(key == current? "selected='selected' >":">")+"></option>")
                                            .attr("value",key)
                                            .text(value));
                            }
                        });
                    }
                })
            </g:javascript>