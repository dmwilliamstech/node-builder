
<div id="Content" class="container">
	<!-- Show page's content -->
        <section class="row-fluid">
            <div class="span12">
                <div class="navbar ${sec.loggedInUserInfo([field: 'authorities']).contains('NBADMINS')? 'navbar-inverse':'navbar-default'} navbar-fixed-top">
                    <div class="navbar-inner">
                        <div class="container">
                            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse" href="">
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </a>
                            <a class="brand" href="${request.contextPath}" title="${node.builder.Config.getApplicationName()}">${node.builder.Config.getApplicationName()}</a>
                            <sec:ifLoggedIn>
                            <div class="nav-collapse">
                                <ul class="nav">
                                    <li class="${request.getServletPath().contains('workflow') ? 'active' : '' }"><a href="${request.contextPath}/workflow" >Workflows</a></li>
                                    <li class="dropdown ${request.getServletPath().contains('manifest') ? 'active' : '' }">
                                        <a href="#"  id="dropdownSandbox" class="dropdown-toggle" data-toggle="dropdown">Sandboxes <b class="caret"></b></a>
                                        <ul class="dropdown-menu">
                                            <li><a href="${request.contextPath}/manifest" >Manifests</a></li>
                                            <li><a href="${request.contextPath}/image">Images</a></li>
                                            <li><a href="${request.contextPath}/instance">Instances</a></li>
                                        </ul>
                                    </li>
                                    <g:if test="${request.getServletPath().contains('manifest') || request.getServletPath().contains('image') || request.getServletPath().contains('instance')}">
                                        <li class="${request.getServletPath().contains('image') ? 'active' : '' }"></li>
                                        <li class="${request.getServletPath().contains('instance') ? 'active' : '' }"></li>
                                    </g:if>
                                    <sec:ifAllGranted roles="ROLE_NBADMINS">
                                        <li class="dropdown ${request.getServletPath().contains('administration') ? 'active' : '' }">
                                            <a href="#" id="dropdownAdmin" class="dropdown-toggle" data-toggle="dropdown">Administrations <b class="caret"></b></a>
                                            <ul class="dropdown-menu">
                                                <li><a id="linkAdminMetric" href="${request.contextPath}/administration/metric">Metrics</a></li>
                                                <li><a id="linkAdminConfig" href="${request.contextPath}/administration/config" >Configurations</a></li>
                                            </ul>
                                        </li>
                                    </sec:ifAllGranted>
                                </ul>
                                <ul class="nav pull-right">
                                    <li class="${request.getServletPath().contains('about') ? 'active' : '' }"><a href="${request.contextPath}/about" >About</a></li>
                                </ul>
                                <ul class="nav pull-right">
                                    <li>
                                        <p class="navbar-text">
                                            <sec:ifLoggedIn>
                                                <sec:username /> (<g:link controller="logout">Logout</g:link>)
                                            </sec:ifLoggedIn>
                                        </p>
                                    </li>
                                </ul>
                            </div><!-- /.nav-collapse -->
                            </sec:ifLoggedIn>
                        </div>
                    </div><!-- /navbar-inner -->
                </div>

            </div>
        </section>
        <!-- /navbar -->

	<g:layoutBody />
	<g:pageProperty name="page.body" />
</div>
