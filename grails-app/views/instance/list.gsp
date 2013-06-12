
<%@ page import="node.builder.Instance" %>
<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'instance.label', default: 'Instance')}" />
	<title><g:message code="default.list.label" args="[entityName]" /></title>
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
<section id="list-instance" class="first">

	<table class="table table-bordered">
		<thead>
			<tr>
			
				<g:sortableColumn property="name" title="${message(code: 'instance.name.label', default: 'Name')}" />
			
				<g:sortableColumn property="ip" title="${message(code: 'instance.ip.label', default: 'Ip')}" />
			
				<g:sortableColumn property="address" title="${message(code: 'instance.address.label', default: 'Address')}" />
			
				<g:sortableColumn property="dateCreated" title="${message(code: 'instance.dateCreated.label', default: 'Date Created')}" />
			
				<g:sortableColumn property="lastUpdated" title="${message(code: 'instance.lastUpdated.label', default: 'Last Updated')}" />
			
			</tr>
		</thead>
		<tbody>
		<g:each in="${instanceInstanceList}" status="i" var="instanceInstance">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
			
				<td><g:link action="show" id="${instanceInstance.id}">${fieldValue(bean: instanceInstance, field: "name")}</g:link></td>
			
				<td>${fieldValue(bean: instanceInstance, field: "ip")}</td>
			
				<td>${fieldValue(bean: instanceInstance, field: "address")}</td>
			
				<td><g:formatDate date="${instanceInstance.dateCreated}" /></td>
			
				<td><g:formatDate date="${instanceInstance.lastUpdated}" /></td>
			
			</tr>
		</g:each>
		</tbody>
	</table>
	<div class="pagination">
		<bs:paginate total="${instanceInstanceTotal}" />
	</div>
</section>

</body>

</html>
