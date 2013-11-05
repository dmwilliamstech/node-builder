
<%@ page import="node.builder.ProjectState; node.builder.metrics.Metrics; node.builder.Project" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<title>${fieldValue(bean: projectInstance, field: "name")} History</title>
</head>

<body>

<h3>${fieldValue(bean: projectInstance, field: "name")}</h3>
<h4>Summary</h4>
<section id="show-summary" class="first">

    <table class="table">
        <tbody>

        <tr class="prop">
            <td valign="top" class="name"><g:message code="project.name.label" default="Average Completion Time" /></td>

            <td valign="top" class="value">${metrics.averageDuration}</td>
        </tr>

        </tbody>
    </table>
</section>

<h4>Historical</h4>
<g:each in="${metrics.workflows}" status="i" var="workflow">
    <h5>${workflow.eventId}</h5>
    <section id="show-history${i}" class="first">

        <table class="table">
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name"><g:message code="project.name.label" default="Resulting Status" /></td>
                    <td valign="top" class="value"><h2><i class="${ProjectState.valueOf(ProjectState, workflow.eventData.data.status).color} ${ProjectState.valueOf(ProjectState, workflow.eventData.data.status).icon}"></i></h2></td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><g:message code="project.name.label" default="Time" /></td>
                    <td valign="top" class="value">${workflow.dateCreated}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><g:message code="project.name.label" default="Runtime" /></td>
                    <td valign="top" class="value">${node.builder.metrics.Metrics.timeStringFromMilliseconds(workflow.duration)}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><g:message code="project.name.label" default="Message" /></td>
                    <td valign="top" class="value">${workflow.message?.toString().replaceAll(/\n/, '<br>')}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><g:message code="project.name.label" default="Artifacts" /></td>
                    <td valign="top" class="value">
                        <g:if test="${workflow.eventData?.data?.artifactUrls != null}">
                            <g:each in="${workflow.eventData?.data?.artifactUrls}" status="j" var="urlInstance">
                                <g:link controller="project" action="artifact" id="${java.net.URLEncoder.encode(urlInstance.url, "UTF-8")}" elementId="fileDownload${urlInstance.title}">${urlInstance.title}</g:link>
                                <br/>
                            </g:each>
                        </g:if>
                        <g:else>
                            <g:each in="${workflow.eventData?.data?.artifactFiles}" status="j" var="file">
                                <g:link controller="project" action="artifact" id="${java.net.URLEncoder.encode(file.path, "UTF-8").replaceAll(/\./, '%2E')}" elementId="fileDownload${file.name}">${file.name}</g:link>
                                <br/>
                            </g:each>
                        </g:else>

                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><g:message code="project.name.label" default="Raw Data" /></td>
                    <td valign="top" class="value"><pre class="pre-scrollable">${workflow as grails.converters.JSON}</pre></td>
                </tr>

            </tbody>
        </table>
    </section>
</g:each>

</body>

</html>
