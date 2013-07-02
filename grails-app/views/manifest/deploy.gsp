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
<div class="container">
    <h2>Deploy <i>${manifest.name}</i> from ${master.hostname} (${master.name})</h2>


</div>
<div class="hero-unit pull-left">
    <h2>Image/Instance</h2>
    <div class="dropdown">
        <a class="dropdown-toggle btn btn-primary" data-toggle="dropdown" href="">Select an Image <b class="caret"></b></a>
        <ul class="dropdown-menu">
        </ul>
    </div>
    <p class="help-block" ><h3><span class="alert alert-success" id="imageName">Select an Image</span></h3></p>
    <div class="control-group">
        <div class="controls">
            <input type="hidden" class="input-xlarge uneditable-input" id="id" value="${master.id}">
        </div>
    </div>
    <div class="pull-left">
        <div class="btn-group">
            <a class="btn btn-success" onclick="handleUpload(this)" id="upload" >Provision</a>
            <a class="btn btn-info" onclick="handleDownload(this)" >Download</a>
        </div>
    </div>
</div>



<div class="span8 pull-left " style="height: 800px">
    <g:render template="graph"/>
</div>
</div>

<g:javascript>
    var json = ${manifest as grails.converters.JSON}
    manifest = json.manifest
    var instances = ${instances as grails.converters.JSON}
</g:javascript>
<script type="text/javascript" src="/node-builder/static/js/deploy.js" ></script>
</body>
</html>