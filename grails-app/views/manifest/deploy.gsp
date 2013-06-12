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
<g:if test="${flash.message && !layout_noflashmessage}">
    <div class="alert alert-info">${flash.message}</div>
</g:if>
<div class="container">
    <h2>Upload to Master</h2>
    <div id="modal" class="modal hide fade in" style="display: none; ">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>
            <h2>Remote Master Connection</h2>
        </div>
        <div class="modal-body">
            <h3>Please Enter Connection Parameters</h3>
            <div class="control-group">
                <div class="controls">
                    <input type="hidden" class="input-xlarge uneditable-input" id="idEdit">
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <input type="hidden" class="input-xlarge uneditable-input" id="isEdit">
                </div>
            </div>

            <div class="control-group">
                <label class="control-label" for="nameEdit"><h4>Name</h4></label>
                <div class="controls">
                    <input type="text" class="input-xlarge" id="nameEdit">
                    <p class="help-block">Name for this configuration</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="hostnameEdit"><h4>Hostname</h4></label>
                <div class="controls">
                    <input type="text" class="input-xlarge" id="hostnameEdit">
                    <p class="help-block">Name of the remote server</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="usernameEdit"><h4>Username</h4></label>
                <div class="controls">
                    <input type="text" class="input-xlarge" id="usernameEdit">
                    <p class="help-block">Name of the remote user</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="privateKeyEdit"><h4>Private Key</h4></label>
                <div class="controls">
                    <textarea class="input-xlarge" id="privateKeyEdit" row-fluids="3"></textarea>
                    <p class="help-block">Key for authorizing user</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="remotePathEdit"><h4>Remote Path</h4></label>
                <div class="controls">
                    <input type="text" class="input-xlarge" id="remotePathEdit">
                    <p class="help-block">Location on the remote server to upload to</p>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <a onclick="handleSaveMaster(this)" class="btn btn-success">Save</a>
            <a href="#" class="btn" data-dismiss="modal">Cancel</a>
        </div>
    </div>


    <div class="dropdown">
        <a class="dropdown-toggle btn btn-primary" data-toggle="dropdown" href="">Select a Master <b class="caret"></b></a>
        <ul class="dropdown-menu">
            <li><a onclick="handleShowModal()" title=""><i class="icon-plus-sign"></i> Add a Master</a></li>
        </ul>
    </div>



</div>
    <div class="control-group">

        <div class="controls">
            <input type="hidden" class="input-xlarge uneditable-input" id="id">
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="name"><h4>Name</h4></label>
        <div class="controls">
            <input type="text" class="input-xlarge uneditable-input" id="name">
            <p class="help-block">Name for this configuration</p>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="hostname"><h4>Hostname</h4></label>
        <div class="controls">
            <input type="text" class="input-xlarge uneditable-input" id="hostname">
            <p class="help-block">Name of the remote server</p>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="username"><h4>Username</h4></label>
        <div class="controls">
            <input type="text" class="input-xlarge uneditable-input" id="username">
            <p class="help-block">Name of the remote user</p>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="privateKey"><h4>Private Key</h4></label>
        <div class="controls">
            <textarea class="input-xlarge uneditable-textarea" id="privateKey" row-fluids="3"></textarea>
            <p class="help-block">Key for authorizing user</p>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label" for="remotePath"><h4>Remote Path</h4></label>
        <div class="controls">
            <input type="text" class="input-xlarge uneditable-input" id="remotePath">
            <p class="help-block">Location on the remote server to upload to</p>
        </div>
    </div>
    <a onclick="handleShowModal(this)" class="btn btn-primary disabled" id="edit" ><i class="icon-pencil icon-white"></i></a>
    <div class="btn-group pull-right">
        <a class="btn btn-success disabled" onclick="handleUpload(this)" id="upload" >Upload</a>
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