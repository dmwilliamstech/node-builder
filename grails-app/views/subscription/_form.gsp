<%@ page import="node.builder.Subscription" %>



			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'organization', 'error')} required">
				<label for="organization" class="control-label"><g:message code="subscription.organization.label" default="Organization" /><span class="required-indicator">*</span></label>
				<div class="controls">
					<g:select id="organization" name="organization.id" from="${node.builder.Organization.list()}" optionKey="id" required="" value="${subscriptionInstance?.organization?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: subscriptionInstance, field: 'organization', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'variables', 'error')} ">
				<label for="variables" class="control-label"><g:message code="subscription.variables.label" default="Variables" /></label>
				<div class="controls">
					
<ul class="one-to-many">
<g:each in="${subscriptionInstance?.variables?}" var="v">
    <li><g:link controller="subscriptionVariable" action="show" id="${v.id}">${v?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="subscriptionVariable" action="create" params="['subscription.id': subscriptionInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subscriptionVariable.label', default: 'SubscriptionVariable')])}</g:link>
</li>
</ul>

					<span class="help-inline">${hasErrors(bean: subscriptionInstance, field: 'variables', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'workflowTag', 'error')} required">
				<label for="workflowTag" class="control-label"><g:message code="subscription.workflowTag.label" default="Workflow Tag" /><span class="required-indicator">*</span></label>
				<div class="controls">
					<g:select id="workflowTag" name="workflowTag.id" from="${node.builder.WorkflowTag.list()}" optionKey="id" required="" value="${subscriptionInstance?.workflowTag?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: subscriptionInstance, field: 'workflowTag', 'error')}</span>
				</div>
			</div>

