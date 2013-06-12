<%@ page import="node.builder.Instance" %>



			<div class="control-group fieldcontain ${hasErrors(bean: instanceInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="instance.name.label" default="Name" /></label>
				<div class="controls">
					<g:textField name="name" value="${instanceInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: instanceInstance, field: 'name', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: instanceInstance, field: 'ip', 'error')} ">
				<label for="ip" class="control-label"><g:message code="instance.ip.label" default="Ip" /></label>
				<div class="controls">
					<g:textField name="ip" value="${instanceInstance?.ip}"/>
					<span class="help-inline">${hasErrors(bean: instanceInstance, field: 'ip', 'error')}</span>
				</div>
			</div>

			<div class="control-group fieldcontain ${hasErrors(bean: instanceInstance, field: 'address', 'error')} ">
				<label for="address" class="control-label"><g:message code="instance.address.label" default="Address" /></label>
				<div class="controls">
					<g:textField name="address" value="${instanceInstance?.address}"/>
					<span class="help-inline">${hasErrors(bean: instanceInstance, field: 'address', 'error')}</span>
				</div>
			</div>

