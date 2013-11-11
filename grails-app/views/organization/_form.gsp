<%@ page import="node.builder.Organization" %>



			<div class="control-group fieldcontain ${hasErrors(bean: organizationInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="organization.name.label" default="Name" /></label>
				<div class="controls">
					<g:textField name="name" value="${organizationInstance?.name}" disabled="disabled"/>
					<span class="help-inline">${hasErrors(bean: organizationInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: organizationInstance, field: 'description', 'error')} ">
				<label for="description" class="control-label"><g:message code="organization.description.label" default="Description" /></label>
				<div class="controls">
					<g:textField name="description" value="${organizationInstance?.description}" disabled="disabled"/>
					<span class="help-inline">${hasErrors(bean: organizationInstance, field: 'description', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: organizationInstance, field: 'subscriptionLevel', 'error')} required">
				<label for="subscriptionLevel" class="control-label"><g:message code="organization.subscriptionLevel.label" default="Subscription Level" /><span class="required-indicator">*</span></label>
				<div class="controls">
					<g:select id="subscriptionLevel" name="subscriptionLevel.id" from="${node.builder.SubscriptionLevel.list()}" optionKey="id" required="" value="${organizationInstance?.subscriptionLevel?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: organizationInstance, field: 'subscriptionLevel', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: organizationInstance, field: 'subscriptions', 'error')} ">
				<label for="subscriptions" class="control-label"><g:message code="organization.subscriptions.label" default="Subscriptions" /></label>
				<div class="controls">
					
<ul class="one-to-many">
<g:each in="${organizationInstance?.subscriptions?}" var="s">
    <li><g:link controller="subscription" action="show" id="${s.id}">${s?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="subscription" action="create" params="['organization.id': organizationInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'subscription.label', default: 'Subscription')])}</g:link>
</li>
</ul>

					<span class="help-inline">${hasErrors(bean: organizationInstance, field: 'subscriptions', 'error')}</span>
				</div>
			</div>

