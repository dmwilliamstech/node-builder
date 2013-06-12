<%@ page import="node.builder.Image" %>



			<div class="control-group fieldcontain ${hasErrors(bean: imageInstance, field: 'name', 'error')} ">
				<label for="name" class="control-label"><g:message code="image.name.label" default="Name" /></label>
				<div class="controls">
					<g:textField name="name" value="${imageInstance?.name}"/>
					<span class="help-inline">${hasErrors(bean: imageInstance, field: 'name', 'error')}</span>
				</div>
			</div>

