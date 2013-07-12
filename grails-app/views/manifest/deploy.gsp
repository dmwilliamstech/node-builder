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
    <h4>Deploy manifest <i>${manifest.name}</i> from master on ${master.hostname} (${master.name})</h4>


</div>
<div class="pull-left">
    <h4>Select an Image</h4>

    <div class="btn-group btn-group-vertical" data-toggle-name="image-select" data-toggle="buttons-radio" >
<g:each in="${images}" status="i" var="image">
        <button type="button" value="${image.imageId}" class="span3 btn btn-small  ${image.id == 1?"active":""}" data-name="${image.name}" data-toggle="button">${image.name}</button>
</g:each>
    </div>
    <div class="control-group">
        <div class="controls">
            <input type="hidden" class="input-xlarge uneditable-input" id="id" value="${master.id}">
        </div>
    </div>
    <div class="pull-left">
        <div class="btn-group">
            <a class="btn btn-success" onclick="handleUpload(this)" id="upload" >Provision</a>
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
</g:javascript>
<script type="text/javascript" src="/node-builder/static/js/deploy.js" ></script>
</body>
</html>