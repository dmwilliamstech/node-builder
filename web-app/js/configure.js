$(document).ready(function() {
console.log(manifest)
    $.each(manifest.nodes, function(ii, node){
        addConfigurations(node, "node");
    });

    $.each(manifest.applications, function(ii, application){
        addConfigurations(application, "application");
    });
//    $.each(node.properties.applications, function(jj, application){
//        console.log(application)
//        $('#applications').find('tbody:last').append('<tr>' +
//            '<td><h3>' + application.name + '</h3></td>' +
//            '<td>' +
//            '    <div class="btn-toolbar">' +
//            '        <a name="application" id="'+application.id+'" class="btn btn-large" onclick="handleIncludeApplication(this)" title="Node added to manifest" >Include</a>' +
//            '        <a class="btn btn-large" title="Info"><i class="icon-info-sign icon-white"></i></a>' +
//            '    </div>' +
//            '</td>' +
//            '</tr>');
//    });
});

function addConfigurations(object, type){
    console.log(object)

    var html =
        '<tr>' +
            '<td>' + "<h3>" + object.name + "</h3>"
    $.each(object.configurations, function(mm, configuration){
        console.log(configuration)
        html += '<div class="control-group">' +
            '<label class="control-label" for="input01">'+ configuration.name +'</label>' +
            '<div class="controls">' +
            '<input type="text" class="input-xlarge" id="'+ configuration.id +'">' +
            '<p class="help-block"></p>' +
            '</div>' +
            '</div>';
    });

    html +=
        '</td>' +
            '</tr>'
    $('#'+type+'s').find('tbody:last').append(
        html
    );
}


function handleDeploy(button){
    //post manifest and forward to configure screen
    $.ajax("manifest/update", {
        data : JSON.stringify({manifest: manifest}),
        contentType : 'application/json',
        type : 'PUT',
        success: function(data){
            console.log(data)
            location = "deploy/" + data.id
        }
    });
}