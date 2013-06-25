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
<h3>Manifest Name</h3>
<div class="control-group">
    <div class="controls">
        <input type="text" class="input-xlarge" id="manifestName">
        <p class="help-block">Save manifest as</p>
    </div>
</div>
<div class="tabtable">
    <ul class="nav nav-tabs">
        <li >
            <a href="#newModal" data-toggle="modal" title="Add an Instance"><strong><i class="icon-plus-sign"></i>New Instance</strong></a>
        </li>
    </ul>
    <div class="tab-content">
    </div>
</div>
<a class="btn btn-info pull-right" onclick="handleConfigure(this)" >Deploy</a>

<div id="newModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="newModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="newModalLabel">New Instance</h3>
    </div>
    <div class="modal-body">
        <div id="newModalAlert"></div>

            <h3>Instance Name</h3>
            <div class="control-group">
                <div class="controls">
                    <input type="text" class="input-xlarge" id="newInstanceName">
                    <p class="help-block">Save instance as</p>
                </div>
            </div>

            <div class="hero-unit">
                <h4>Select Suite</h4>
                <p class="help-block">List of application suites for adding to the manifest</p>
                <table id="newNodes" class="table table-striped">
                    <thead>
                    <tr>
                        <th><h4>Suite</h4></th>
                        <th><h4>Actions</h4></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <div class="hero-unit">
                <h4>Select Applications</h4>
                <p class="help-block">List of individual applications for adding to the manifest</p>
                <table id="newApplications" class="table table-striped">
                    <thead>
                    <tr>
                        <th><h4>Application</h4></th>
                        <th><h4>Actions</h4></th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>

        </div>
    </div>
    <div class="modal-footer">
        <button class="btn" onclick="handleCloseNewInstance(this)" >Close</button>
        <button class="btn btn-primary" onclick="handleSaveNewInstance(this)" >Save</button>
    </div>
</div>

<g:javascript>
    <g:javascript>
        var manifest = ${ manifestInstance ?: "null" }
        if(manifest)
            manifest = manifest
        else
            manifest = {instances: []}
        var flavors = ${flavors as grails.converters.JSON}
    </g:javascript>
</g:javascript>

<script type="text/javascript" src="/node-builder/static/js/index.js" ></script>

</body>
</html>