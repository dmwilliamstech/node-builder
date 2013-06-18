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
                                <li class="active"><a title="Build a node">Build</a></li>
                                <li><a title="Configure your Node">Configure</a></li>
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
<h1>Name</h1>
<div class="control-group">
    <div class="controls">
        <input type="text" class="input-xlarge" id="manifestName">
        <p class="help-block">Save manifest as</p>
    </div>
</div>

<div class="hero-unit">
<h2>Select Suite</h2>
<p class="help-block">List of application suites for adding to the manifest</p>
<table id="nodes" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Suite</h2></th>
        <th><h2>Actions</h2></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
</div>
<div class="hero-unit">
<h2>Select Applications</h2>
<p class="help-block">List of individual applications for adding to the manifest</p>
<table id="applications" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Application</h2></th>
        <th><h2>Actions</h2></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
</div>
<a class="btn btn-large btn-info pull-right" onclick="handleConfigure(this)" >Configure</a>

<script type="text/javascript" src="/node-builder/static/js/index.js" ></script>

</body>
</html>