<%@ page import="node.builder.SubscriptionVariable" %>



			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionVariableInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="subscriptionVariable.name.label" default="Name" /></label>
				<div class="controls">
					<g:textField name="name" value="${subscriptionVariableInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: subscriptionVariableInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionVariableInstance, field: 'subscription', 'error')} required">
				<label for="subscription" class="control-label"><g:message code="subscriptionVariable.subscription.label" default="Subscription" /><span class="required-indicator">*</span></label>
				<div class="controls">
					<g:select id="subscription" name="subscription.id" from="${node.builder.Subscription.list()}" optionKey="id" required="" value="${subscriptionVariableInstance?.subscription?.id}" class="many-to-one"/>
					<span class="help-inline">${hasErrors(bean: subscriptionVariableInstance, field: 'subscription', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionVariableInstance, field: 'value', 'error')} ">
				<label for="value" class="control-label"><g:message code="subscriptionVariable.value.label" default="Value" /></label>
				<div class="controls">
					<g:textField name="value" value="${subscriptionVariableInstance?.value}"/>
					<span class="help-inline">${hasErrors(bean: subscriptionVariableInstance, field: 'value', 'error')}</span>
				</div>
			</div>

