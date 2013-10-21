<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="kickstart" />
    <title>Sonar Issues</title>

    <style>
    body {
        width: 100%;
        margin: 40px auto;
        font-family: 'trebuchet MS', 'Lucida sans', Arial;
        font-size: 11px;
        color: #444;
    }

    table {
        *border-collapse: collapse; /* IE7 and lower */
        border-spacing: 0;
        width: 100%;
        table-layout: fixed;
    }

    .bordered {
        border: solid #ccc 1px;
        -moz-border-radius: 6px;
        -webkit-border-radius: 6px;
        border-radius: 6px;
        -webkit-box-shadow: 0 1px 1px #ccc;
        -moz-box-shadow: 0 1px 1px #ccc;
        box-shadow: 0 1px 1px #ccc;
    }

    .bordered tr:hover {
        background: #fbf8e9;
        -o-transition: all 0.1s ease-in-out;
        -webkit-transition: all 0.1s ease-in-out;
        -moz-transition: all 0.1s ease-in-out;
        -ms-transition: all 0.1s ease-in-out;
        transition: all 0.1s ease-in-out;
    }

    .bordered td, .bordered th {
        border-left: 1px solid #ccc;
        border-top: 1px solid #ccc;
        padding: 10px;
        text-align: left;
        word-wrap: break-word;
    }

    .bordered th {
        background-color: #dce9f9;
        background-image: -webkit-gradient(linear, left top, left bottom, from(#ebf3fc), to(#dce9f9));
        background-image: -webkit-linear-gradient(top, #ebf3fc, #dce9f9);
        background-image:    -moz-linear-gradient(top, #ebf3fc, #dce9f9);
        background-image:     -ms-linear-gradient(top, #ebf3fc, #dce9f9);
        background-image:      -o-linear-gradient(top, #ebf3fc, #dce9f9);
        background-image:         linear-gradient(top, #ebf3fc, #dce9f9);
        -webkit-box-shadow: 0 1px 0 rgba(255,255,255,.8) inset;
        -moz-box-shadow:0 1px 0 rgba(255,255,255,.8) inset;
        box-shadow: 0 1px 0 rgba(255,255,255,.8) inset;
        border-top: none;
        text-shadow: 0 1px 0 rgba(255,255,255,.5);
    }

    .bordered td:first-child, .bordered th:first-child {
        border-left: none;
    }

    .bordered th:first-child {
        -moz-border-radius: 6px 0 0 0;
        -webkit-border-radius: 6px 0 0 0;
        border-radius: 6px 0 0 0;
    }

    .bordered th:last-child {
        -moz-border-radius: 0 6px 0 0;
        -webkit-border-radius: 0 6px 0 0;
        border-radius: 0 6px 0 0;
    }

    .bordered th:only-child{
        -moz-border-radius: 6px 6px 0 0;
        -webkit-border-radius: 6px 6px 0 0;
        border-radius: 6px 6px 0 0;
    }

    .bordered tr:last-child td:first-child {
        -moz-border-radius: 0 0 0 6px;
        -webkit-border-radius: 0 0 0 6px;
        border-radius: 0 0 0 6px;
    }

    .bordered tr:last-child td:last-child {
        -moz-border-radius: 0 0 6px 0;
        -webkit-border-radius: 0 0 6px 0;
        border-radius: 0 0 6px 0;
    }

    .break-word {
        word-wrap: break-word;
    }
    </style>
</head>

<body>
<table class="bordered">
    <thead>
    <tr>
        <th>Issue</th>
    </tr>
    </thead>
    <tbody>
    <% issues.each { issue ->  %>
    <tr id="issueEntry${issue.key}" >

        <td class="break-word">
            File: ${issue.component}
            <br/>
            Rule: ${issue.rule}
            <br/>
            Status: ${issue.status}
            <br/>
            Severity: ${issue.severity}
            <br/>
            Message: ${issue.message}
            <br/>
            Line Number: ${issue.line}
            <br/>
            Created: ${issue.creationDate}
            <br/>
            Updated: ${issue.updateDate}
        </td>

    </tr>
    <% } %>
    </tbody>
</table>
</body>

</html>
