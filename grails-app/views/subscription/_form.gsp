<%@ page import="grails.converters.JSON; node.builder.Subscription" %>



			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'organization', 'error')} required">
				<label for="organization" class="control-label"><g:message code="subscription.organization.label" default="Organization" /></label>
				<div class="controls">
                    <sec:ifAllGranted roles="ROLE_ADMINS">
                        <sec:ifNotGranted roles="ROLE_NBADMINS">
                            <g:select id="organization" name="organization.id" from="${organizations}" optionKey="id" required="" value="${subscriptionInstance?.organization?.id}" class="many-to-one"/>
                        </sec:ifNotGranted>
                    </sec:ifAllGranted>
                    <sec:ifAllGranted roles="ROLE_NBADMINS">
                        <g:select id="organization" name="organization.id" from="${node.builder.Organization.list()}" optionKey="id" required="" value="${subscriptionInstance?.organization?.id}" class="many-to-one"/>
                    </sec:ifAllGranted>
					<span class="help-inline">${hasErrors(bean: subscriptionInstance, field: 'organization', 'error')}</span>
				</div>
			</div>



			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'workflowTag', 'error')} required">
				<label for="workflowTag" class="control-label"><g:message code="subscription.workflowTag.label" default="Select A Project" /></label>
				<div class="controls">
					<g:select onchange="handleTagSelected(this);" id="workflowTag" name="workflowTag.id" from="${node.builder.WorkflowTag.list()}" optionKey="id" required="" value="${subscriptionInstance?.workflowTag?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: subscriptionInstance, field: 'workflowTag', 'error')}</span>
				</div>
			</div>

            <div id="variables"></div>

  <g:javascript>
    var variables = ${subscriptionInstance.variables as JSON}

    $(document).ready(function(){
        var id = $("#workflowTag.many-to-one option:selected").val()
        var path = location.pathname.replace(/subscription.*/, 'workflowTag/variables/' + id)
        $('#variables').load(path, function(){
            $.each(variables, function(index, variable){
                $('#' + variable.name).val(variable.value)
            })
        })
    })



    function handleTagSelected(select){
        var id = $("#workflowTag.many-to-one option:selected").val()
        var path = location.pathname.replace(/subscription.*/, 'workflowTag/variables/' + id)
        $('#variables').load(path)
    }

  </g:javascript>