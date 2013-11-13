<div id="variables" class="">
    <div class="control-group fieldcontain ${hasErrors(bean: subscriptionInstance, field: 'variables', 'error')} required">
        <label for="variables" class="control-label"><g:message code="subscription.variables.label" default="Variables" /></label>
        <div class="controls">
            <table class="table table-bordered">
            <tr>
                <td style="width: 80%">
                   <ul style="list-style-type: none;">
                        <g:each in="${availableVariables}" var="variable">
                            <li>
                                <g:message code="subscription.workflowTagVariable.label" default="${variable}" />
                                <br>
                                <input type="text" style="width: 80%" name="workflowTagVariables.${variable}" id="${variable}" value=""/>
                            </li>
                            <hr>
                        </g:each>
                   </ul>
                </td>
                <td>

                </td>
            </tr>
            </table>
        </div>
    </div>
</div>