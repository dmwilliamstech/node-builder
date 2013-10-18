<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="kickstart" />
    <title>Sonar Issues</title>
</head>

<body>
<section id="list" class="first">

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Key</th>
            <th>Component</th>
            <th>Project</th>
            <th>Rule</th>
            <th>Status</th>
            <th>Severity</th>
            <th>Message</th>
            <th>Line</th>
            <th>Created</th>
            <th>Updated</th>
        </tr>
        </thead>
        <tbody>
        <% issues.each { issue ->  %>
            <tr id="issueEntry${issue.key}" >

                <td>${issue.key}</td>

                <td>${issue.component}</td>

                <td>${issue.project}</td>

                <td>${issue.rule}</td>

                <td>${issue.status}</td>

                <td>${issue.severity}</td>

                <td>${issue.message}</td>

                <td>${issue.line}</td>

                <td>${issue.creationDate}</td>

                <td>${issue.updateDate}</td>

            </tr>
        <% } %>
        </tbody>
    </table>
</section>

</body>

</html>
