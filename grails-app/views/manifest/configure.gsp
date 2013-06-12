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
                        <a class="brand" title="OpenDX">OpenDX</a>
                        <div class="nav-collapse">
                            <ul class="nav">
                                <li><a  title="Build a node">Build</a></li>
                                <li class="active" ><a  title="Configure your Node">Configure</a></li>
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
<!-- print system messages (infos, warnings, etc) - not validation errors -->
<div id="alert">
<g:if test="${flash.message && !layout_noflashmessage}">
    <div class="alert alert-info">${flash.message}</div>
</g:if>
</div>
<h1>Image/Instance</h1>
<div class="dropdown">
    <a class="dropdown-toggle btn btn-primary" data-toggle="dropdown" href="">Select an Image <b class="caret"></b></a>
    <ul class="dropdown-menu">
    </ul>
</div>
<p class="help-block" ><h3><span class="alert alert-success" id="imageName"></span></h3></p>
<div class="control-group">
    <label class="control-label" for="instanceName"><h4>Instance Name</h4></label>
    <div class="controls">
        <input type="text" class="input-xlarge" onchange="handleInstanceNameChange(this)" id="instanceName">
        <p class="help-block">Name applied to the image after starting and used for access</p>
    </div>
</div>

<h1>Configure Suite</h1>
<table id="nodes" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Suite</h2></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<h1>Configure Applications</h1>
<table id="applications" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Application</h2></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<a class="btn btn-large btn-info pull-right" onclick="handleDeploy(this)" >Deploy</a>


<g:javascript>
    var json = ${manifest as grails.converters.JSON}
    manifest = json.manifest
</g:javascript>
<script type="text/javascript" src="/node-builder/static/js/configure.js" ></script>
</body>
</html>