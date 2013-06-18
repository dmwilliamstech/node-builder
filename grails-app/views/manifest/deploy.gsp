<!DOCTYPE html>
<html>
<head>
    <title>Node Builder</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <meta name="layout" content="kickstart" />
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
                        <a class="brand"  title="OpenDX">OpenDX</a>
                        <div class="nav-collapse">
                            <ul class="nav">
                                <li><a title="Build a node">Build</a></li>
                                <li><a title="Configure your Node">Configure</a></li>
                                <li class="active"><a title="Deploy your node">Deploy</a></li>
                            </ul>
                        </div><!-- /.nav-collapse -->
                    </div>
                </div><!-- /navbar-inner -->
            </div>

        </div>
    </section>
    <!-- /navbar -->
</section>
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
<g:if test="${flash.message && !layout_noflashmessage}">
    <div class="alert alert-info">${flash.message}</div>
</g:if>
</div>
<div class="container">
    <h2>Deploy <i>${manifest.name}</i> to <i>${manifest.manifest.instanceName}</i> from ${master.hostname} (${master.name})</h2>

</div>
    <div class="control-group">

        <div class="controls">
            <input type="hidden" class="input-xlarge uneditable-input" id="id" value="${master.id}">
        </div>
    </div>
    <div class="btn-group">
        <a class="btn btn-success" onclick="handleUpload(this)" id="upload" >Provision</a>
        <a class="btn btn-info" onclick="handleDownload(this)" >Download</a>
    </div>

</div>

<g:javascript>
    var json = ${manifest as grails.converters.JSON}
    manifest = json.manifest
</g:javascript>
<script type="text/javascript" src="/node-builder/static/js/deploy.js" ></script>
</body>
</html>