

<!doctype html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="layout" content="kickstart" />
	<g:set var="entityName" value="${message(code: 'about.label', default: 'About')}" />
	<title>About</title>
</head>

<body>

<h3 id='application-name'>${name}</h3>
<h4 id='application-version' class="text-info">Version ${version}</h4>
<h4 id='application-reference' class="text-info">Reference ${reference}</h4>
<hr>
<section id="list-dependency-build" class="first">
    <h4>Dependencies (Build)</h4>
	<table class="table table-bordered">
		<thead>
			<tr>
				<th>${message(code: 'dependency.name.label', default: 'Name')}</th>

				<th>${message(code: 'dependency.group.label', default: 'Group')}</th>

				<th>${message(code: 'dependency.version.label', default: 'Version')}</th>
			</tr>
		</thead>
		<tbody>
        <g:each in="${dependencies.build.dependency}" status="i" var="dependency">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>
                    ${dependency.@name}
                </td>
                <td>
                    ${dependency.@group}
                </td>
                <td>
                    ${dependency.@version}
                </td>
            </tr>
        </g:each>

		</tbody>
	</table>
</section>

<section id="list-dependency-compile" class="first">
    <h4>Dependencies (Compile)</h4>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>${message(code: 'dependency.name.label', default: 'Name')}</th>

            <th>${message(code: 'dependency.group.label', default: 'Group')}</th>

            <th>${message(code: 'dependency.version.label', default: 'Version')}</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${dependencies.compile.dependency}" status="i" var="dependency">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>
                    ${dependency.@name}
                </td>
                <td>
                    ${dependency.@group}
                </td>
                <td>
                    ${dependency.@version}
                </td>
            </tr>
        </g:each>

        </tbody>
    </table>
</section>

<section id="list-dependency-test" class="first">
    <h4>Dependencies (Test)</h4>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>${message(code: 'dependency.name.label', default: 'Name')}</th>

            <th>${message(code: 'dependency.group.label', default: 'Group')}</th>

            <th>${message(code: 'dependency.version.label', default: 'Version')}</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${dependencies.test.dependency}" status="i" var="dependency">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>
                    ${dependency.@name}
                </td>
                <td>
                    ${dependency.@group}
                </td>
                <td>
                    ${dependency.@version}
                </td>
            </tr>
        </g:each>

        </tbody>
    </table>
</section>

<section id="list-dependency-runtime" class="first">
    <h4>Dependencies (Runtime)</h4>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>${message(code: 'dependency.name.label', default: 'Name')}</th>

            <th>${message(code: 'dependency.group.label', default: 'Group')}</th>

            <th>${message(code: 'dependency.version.label', default: 'Version')}</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${dependencies.runtime.dependency}" status="i" var="dependency">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>
                    ${dependency.@name}
                </td>
                <td>
                    ${dependency.@group}
                </td>
                <td>
                    ${dependency.@version}
                </td>
            </tr>
        </g:each>

        </tbody>
    </table>
</section>

<section id="list-dependency-provide" class="first">
    <h4>Dependencies (Provided)</h4>
    <table class="table table-bordered">
        <thead>
        <tr>
            <th>${message(code: 'dependency.name.label', default: 'Name')}</th>

            <th>${message(code: 'dependency.group.label', default: 'Group')}</th>

            <th>${message(code: 'dependency.version.label', default: 'Version')}</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${dependencies.provided.dependency}" status="i" var="dependency">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>
                    ${dependency.@name}
                </td>
                <td>
                    ${dependency.@group}
                </td>
                <td>
                    ${dependency.@version}
                </td>
            </tr>
        </g:each>

        </tbody>
    </table>
</section>
</body>

</html>
