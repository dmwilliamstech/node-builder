

Admin
<section id="list-subscription" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'subscription.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'subscription.lastUpdated.label', default: 'Last Updated')}" />
			
				<th><g:message code="subscription.organization.label" default="Organization" /></th>
			
				<th><g:message code="subscription.variables.label" default="Variables" /></th>
			
				<th><g:message code="subscription.workflowTag.label" default="Workflow Tag" /></th>
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${subscriptionInstanceList}" status="i" var="subscriptionInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${subscriptionInstance.id}">${fieldValue(bean: subscriptionInstance, field: "dateCreated")}</g:link></td>
			
				<td><g:formatDate date="${subscriptionInstance.lastUpdated}" /></td>
			
				<td>${fieldValue(bean: subscriptionInstance, field: "organization")}</td>
			
				<td>${fieldValue(bean: subscriptionInstance, field: "variables")}</td>
			
				<td>${fieldValue(bean: subscriptionInstance, field: "workflowTag")}</td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${subscriptionInstanceTotal}" />
	</div>
</section>
