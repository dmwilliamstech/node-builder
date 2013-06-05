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
                        <a class="brand" href="" title="OpenDX">OpenDX</a>
                        <div class="nav-collapse">
                            <ul class="nav">
                                <li class="active"><a href="" title="Build a node">Build</a></li>
                                <li><a href="" title="Configure your Node">Configure</a></li>
                                <li><a href="" title="Deploy your node">Deploy</a></li>
                            </ul>
                        </div><!-- /.nav-collapse -->
                    </div>
                </div><!-- /navbar-inner -->
            </div>

        </div>
    </section>
    <!-- /navbar -->
</section>
<h1>Configure Node</h1>
<table id="nodes" class="table table-striped">
    <thead>
    <tr>
        <th><h2>Node</h2></th>
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
    var manifest = ${manifest.manifest as grails.converters.JSON}
</g:javascript>
<script type="text/javascript" src="/node-builder/static/js/configure.js" ></script>
</body>
</html>