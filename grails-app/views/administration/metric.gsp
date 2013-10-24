<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'metrics.label', default: 'Metrics')}" />
    <title>${entityName}</title>
</head>
<body>
        <h4>Metrics</h4>
        <div class="panel panel-default">
            <div class="panel-heading">
                Up Time
            </div>
            <div class="panel-body">
                ${metrics.upTime}
            </div>
        </div>

</body>
</html>