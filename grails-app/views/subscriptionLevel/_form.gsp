<%@ page import="node.builder.SubscriptionLevel" %>



			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionLevelInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="subscriptionLevel.name.label" default="Name" /></label>
				<div class="controls">
					<g:textField name="name" value="${subscriptionLevelInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: subscriptionLevelInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionLevelInstance, field: 'description', 'error')} ">
				<label for="description" class="control-label"><g:message code="subscriptionLevel.description.label" default="Description" /></label>
				<div class="controls">
					<g:textArea name="description" cols="40" rows="5" maxlength="10000" value="${subscriptionLevelInstance?.description}"/>
					<span class="help-inline">${hasErrors(bean: subscriptionLevelInstance, field: 'description', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionLevelInstance, field: 'organizations', 'error')} ">
				<label for="organizations" class="control-label"><g:message code="subscriptionLevel.organizations.label" default="Organizations" /></label>
				<div class="controls">
					
<ul class="one-to-many">
<g:each in="${subscriptionLevelInstance?.organizations?}" var="o">
    <li><g:link controller="organization" action="show" id="${o.id}">${o?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="organization" action="create" params="['subscriptionLevel.id': subscriptionLevelInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'organization.label', default: 'Organization')])}</g:link>
</li>
</ul>

					<span class="help-inline">${hasErrors(bean: subscriptionLevelInstance, field: 'organizations', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: subscriptionLevelInstance, field: 'subscriptionCount', 'error')} required">
				<label for="subscriptionCount" class="control-label"><g:message code="subscriptionLevel.subscriptionCount.label" default="Subscription Count" /><span class="required-indicator">*</span></label>
				<div class="controls">
					<g:field type="number" name="subscriptionCount" required="" value="${subscriptionLevelInstance.subscriptionCount}"/>
					<span class="help-inline">${hasErrors(bean: subscriptionLevelInstance, field: 'subscriptionCount', 'error')}</span>
				</div>
			</div>

