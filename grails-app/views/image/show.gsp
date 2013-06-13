
<%@ page import="node.builder.Image" %>
<!doctype html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'image.label', default: 'Image')}" />
	<title><g:message code="default.show.label" args="[entityName]" /></title>
</head>

<body>
<section id="navbar" class="">
    <section class="row-fluid">
        <div class="span12">
            <div class="navbar">
                <div class="navbar-inner">
                    <div class="container">
                        <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse" href="">
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </a>
                        <a class="brand" title="OpenDX">OpenDX</a>
                        <div class="nav-collapse">
                            <ul class="nav">
                                <li><a  title="Build a node">Build</a></li>
                                <li><a  title="Configure your Node">Configure</a></li>
                                <li><a title="Deploy your node">Deploy</a></li>
                            </ul>
                        </div><!-- /.nav-collapse -->
                    </div>
                </div><!-- /navbar-inner -->
            </div>

        </div>
    </section>
    <!-- /navbar -->
</section>
<section id="show-image" class="first">

	<table class="table">
		<tbody>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="image.name.label" default="Name" /></td>
				
				<td valign="top" class="value">${fieldValue(bean: imageInstance, field: "name")}</td>
				
			</tr>

            <tr class="prop">
                <td valign="top" class="id"><g:message code="image.id.label" default="ID" /></td>

                <td valign="top" class="value">${fieldValue(bean: imageInstance, field: "imageId")}</td>

            </tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="image.dateCreated.label" default="Date Created" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${imageInstance?.dateCreated}" /></td>
				
			</tr>
		
			<tr class="prop">
				<td valign="top" class="name"><g:message code="image.lastUpdated.label" default="Last Updated" /></td>
				
				<td valign="top" class="value"><g:formatDate date="${imageInstance?.lastUpdated}" /></td>
				
			</tr>
		
		</tbody>
	</table>
</section>

</body>

</html>
