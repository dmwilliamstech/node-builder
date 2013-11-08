<%@ page import="grails.converters.JSON; node.builder.Workflow" %>
            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'workflowType', 'error')} required">
                <label for="workflowType" class="control-label"><g:message code="workflow.workflowType.label" default="Workflow Type" /><span class="required-indicator">*</span></label>
                <div class="controls">
                    <g:select id="workflowType" name="workflowType.id" from="${node.builder.WorkflowType.list()}" optionKey="id" required="" value="${workflowInstance?.workflowType?.id}" class="many-to-one"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'workflowType', 'error')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'name', 'error')} ">
                <label for="name" class="control-label"><g:message code="workflow.name.label" default="Name" /></label>
                <div class="controls">
                    <g:textField name="name" value="${workflowInstance?.name}"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'name', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'tags', 'error')} ">
                <label for="tags" class="control-label"><g:message code="workflow.group.label" default="Tags" /></label>
                <div class="controls">
                    <g:textField id="workflowTags" name="tags" value=""/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'tags', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'description', 'error')} ">
                <label for="description" class="control-label"><g:message code="workflow.description.label" default="Description" /></label>
                <div class="controls">
                    <g:textField name="description" value="${workflowInstance?.description}"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'description', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'location', 'error')} ">
                <label for="location" class="control-label"><g:message code="workflow.location.label" default="Location" /></label>
                <div class="controls">
                    <g:textField name="location" value="${workflowInstance?.location}"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'location', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'processDefinitionKey', 'error')} ">
                <label for="processDefinitionKey" class="control-label"><g:message code="workflow.processDefinitionKey.label" default="Process Definition ID" /></label>
                <div class="controls">
                    <select name="processDefinitionKey" id="processDefinitionKey" value="" >
                        <option id="0"><g:message code="workflow.processDefinitionKey.default.option" default="Please provide BPMN workflow" /></option>
                    </select>
                    %{--<g:textField name="processDefinitionKey" value="${workflowInstance?.processDefinitionKey}"/>--}%
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'processDefinitionKey', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'bpmn', 'error')} ">
				<label for="bpmnEditor" class="control-label"><g:message code="workflow.bpmn.label" default="Bpmn" /></label>
				<div class="controls">
                    <div id="bpmnEditor" style="width:700px;height:200px"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'bpmn', '<i class="icon-exclamation-sign"></i>')}</span>
				</div>
			</div>
            <g:textArea id="bpmnTextArea" rows="20" class="span8" name="bpmn" value="${workflowInstance?.bpmn}"/>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'active', 'error')} ">
                <label for="active" class="control-label"><g:message code="workflow.active.label" default="Workflow should be active?" /></label>
                <div class="controls">
                    <g:checkBox rows="20" class="span8" name="active" value="${workflowInstance?.active}"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'active', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <div class="control-group fieldcontain ${hasErrors(bean: workflowInstance, field: 'subscribable', 'error')} ">
                <label for="subscribable" class="control-label"><g:message code="workflow.active.label" default="Users can subscribe?" /></label>
                <div class="controls">
                    <g:checkBox rows="20" class="span8" name="subscribable" value="${workflowInstance?.subscribable}"/>
                    <span class="help-inline">${hasErrors(bean: workflowInstance, field: 'subscribable', '<i class="icon-exclamation-sign"></i>')}</span>
                </div>
            </div>

            <g:hiddenField name="state" value="OK"/>
            <g:hiddenField name="message" value=""/>


            <g:javascript library="xml2json" />
            <g:javascript library="ace" />
            <g:javascript library="mode_xml" />

            <g:javascript library="bootstrapTags" />
            <link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-tagsinput.css')}" type="text/css">
            <g:javascript>
                $(document).ready(function(){
                    var x2js = new X2JS();
                    var editor = ace.edit("bpmnEditor");
                    var Mode = ace.require('ace/mode/xml').Mode;

                    editor.getSession().setMode(new Mode());
                    var textarea = $('#bpmnTextArea').hide();

                    parseProcessIds(textarea.val(), "${workflowInstance.processDefinitionKey}")

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
                        var processIds = {}
                        var json = x2js.xml_str2json( definition )
                        if(json.definitions != null && json.definitions.process != null){
                            var processes = json.definitions.process
                            if( !(processes instanceof Array) ){
                                processes = [processes]
                            }
                            $.each(processes, function(index, process){
                                processIds[ process._id ] = process._id
                            });
                        }

                        createProcessIdOptions(processIds, current)
                    }

                    function createProcessIdOptions(processIds, current){
                        $.each(processIds, function(key, value) {
                            if($("#processDefinitionKey option[value='"+value+"']").length == 0){
                                $("#processDefinitionKey option[id=0]").remove()
                                $('#processDefinitionKey')
                                    .append($("<option></option>")
                                            .attr("selected",(key == current || $("#processDefinitionKey option").length == 0 ? "selected":"selected"))
                                            .attr("value",key)
                                            .text(value));
                            }
                        });
                    }

                   $('#workflowTags').tagsinput({
                      itemValue: 'id',
                      itemText: 'name',
                      maxTags: 3,
                      typeahead: {
                        source: function(query) {
                          return $.getJSON(location.pathname.replace(/workflow.*/,'workflow/tags'))
                        }
                      }
                   })


                    var tags = ${(workflowInstance.tags?: [] )as JSON};

                    $.each(tags, function(index, tag){
                            $('#workflowTags').tagsinput('add', tag)
                    })

                    $('div.bootstrap-tagsinput').width(206)
                })
            </g:javascript>