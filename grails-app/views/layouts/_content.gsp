
<div id="Content" class="container">
	<!-- Show page's content -->
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
                            <a class="brand" href="${request.contextPath}" title="OpenDX">OpenDX</a>
                            <div class="nav-collapse">
                                <ul class="nav">
                                    <li class="${request.getServletPath().contains('manifest') ? 'active' : '' }"><a href="${request.contextPath}/manifest" >Manifests</a></li>
                                    <li class="${request.getServletPath().contains('image') ? 'active' : '' }"><a href="${request.contextPath}/image">Images</a></li>
                                    <li class="${request.getServletPath().contains('instance') ? 'active' : '' }"><a href="${request.contextPath}/instance">Instances</a></li>
                                </ul>
                            </div><!-- /.nav-collapse -->
                        </div>
                    </div><!-- /navbar-inner -->
                </div>

            </div>
        </section>
        <!-- /navbar -->
	<g:layoutBody />
	<g:pageProperty name="page.body" />
</div>
