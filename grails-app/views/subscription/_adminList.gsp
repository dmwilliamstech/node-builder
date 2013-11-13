<%@ page import="node.builder.WorkflowState" %>

<section id="list-subscription" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>

                <th><g:message code="subscription.workflowTag.label" default="Projects" /></th>

                <th><g:message code="subscription.organization.label" default="Organization" /></th>

                <th><g:message code="subscription.status.label" default="Status" /></th>
			
				<th><g:message code="subscription.variables.label" default="Variables" /></th>

                <th><g:message code="subscription.variables.label" default="Workflows" /></th>

			</tr>
		</thead>
		<tbody>
		<g:each in="${subscriptionInstanceList}" status="i" var="subscriptionInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

				<td><g:link action="edit" id="${subscriptionInstance.id}">${fieldValue(bean: subscriptionInstance, field: "workflowTag")}</g:link></td>

				<td>${fieldValue(bean: subscriptionInstance, field: "organization")}</td>

                <td><h2><i class="centre center ${((WorkflowState)subscriptionInstance.subscriptionState()).color} ${((WorkflowState)subscriptionInstance.subscriptionState()).icon}"></i></h2></td>

                <td>
                    <g:each in="${subscriptionInstance.variables}" var="variable">
                        <div class="span2">${variable.name}</div> <div class="span2"><div class="label label-info">${variable.value}</div></div>
                        <br>
                    </g:each>
                </td>

                <td>
                <g:each in="${subscriptionInstance.workflowTag.workflows}" var="workflow">
                    <div>
                         <g:link controller="workflow" action="show" id="${workflow.id}">${fieldValue(bean: workflow, field: "name")}</g:link>

                        <i style="font-size: 1.5em" class="pull-right ${(workflow.state).color} ${(workflow.state).icon}"></i>
                        <div class="pull-right label ${workflow.active? "label-success":"label-default"}"><g:formatBoolean boolean="${workflow.active}" false="Inactive" true="Active"/></div>
                    </div>
                     <hr>
                </g:each>
                </td>

			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${subscriptionInstanceTotal}" />
	</div>
</section>
