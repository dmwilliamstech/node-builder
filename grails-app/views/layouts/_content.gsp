
<div id="Content" class="container">
	<!-- Show page's content -->
    <div class="pull-right">
    <sec:ifNotLoggedIn>
        <g:link controller="login" action="auth">Login</g:link>
    </sec:ifNotLoggedIn>
    <sec:ifLoggedIn>
        Welcome <sec:username />! (<g:link controller="logout">Logout</g:link>)
    </sec:ifLoggedIn>
    </div>
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
                                <ul class="nav pull-right">
                                    <li class="${request.getServletPath().contains('about') ? 'active' : '' }"><a href="${request.contextPath}/about" >About</a></li>
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
