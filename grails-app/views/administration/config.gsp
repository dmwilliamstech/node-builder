<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="kickstart" />
    <g:set var="entityName" value="${message(code: 'configs.label', default: 'Config')}" />
    <title>${entityName}</title>
</head>
<body>
    <div id='configListPanel' class="panel panel-default">
        <div class="panel-heading">
            Configurations
        </div>

        <table class="table">
           <tr>
               <td>
                   <g:link elementId="configSubscriptionLevelsLink" controller="subscriptionLevel" action="list">Subscription Levels</g:link>
               </td>
           </tr>
           <tr>
               <td>
                    <g:link elementId="configOrganizationsLink" controller="organization" action="list">Organizations</g:link>
               </td>
           </tr>
        </table>

    </div>

</body>
</html>