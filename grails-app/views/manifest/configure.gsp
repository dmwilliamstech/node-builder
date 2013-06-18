<!DOCTYPE html>
<html>
<head>
    <title>Node Builder</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <meta name="layout" content="kickstart" />
</head>
<body>
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
<g:if test="${flash.message && !layout_noflashmessage}">
    <div class="alert alert-info">${flash.message}</div>
</g:if>
</div>
<div class="hero-unit">
<h2>Configure Suite</h2>
<table id="nodes" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Suite</h2></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
</div>
<div class="hero-unit">
<h2>Configure Applications</h2>
<table id="applications" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Application</h2></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
</div>
<a class="btn btn-large btn-info pull-right" onclick="handleDeploy(this)" >Deploy</a>


<g:javascript>
    var json = ${manifest as grails.converters.JSON}
    manifest = json.manifest
</g:javascript>
<script type="text/javascript" src="/node-builder/static/js/configure.js" ></script>
</body>
</html>