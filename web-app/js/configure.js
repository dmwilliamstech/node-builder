$(document).ready(function() {
    console.log(manifest)
    $.each(manifest.nodes, function(id, node){
        addConfigurations(id, node, "node");
    });

    $.each(manifest.applications, function(id, application){
        addConfigurations(id, application, "application");
    });
});

function addConfigurations(id, object, type){
    var html =
        '<tr>' +
        '<td>' + "<h3>" + object.name + "</h3>"
    $.each(object.configurations, function(mm, configuration){

        html += '<div class="control-group">' +
            '<label class="control-label" for="'+ mm +'">'+ configuration.name +'</label>' +
            '<div class="controls">' +
            '<input type="text" class="input-xlarge" id="'+ mm +'"  name="'+ id +'" onchange="handleInputChange(this, \''+type+'s\')">' +
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

function handleInputChange(input, type){
    manifest[type][$(input)[0].name].configurations[$(input)[0].id].value = $(input).val()
}

function handleDeploy(button){
    //post manifest and forward to configure screen
    $.ajax("update/" +manifest.id , {
        data : JSON.stringify(manifest),
        contentType : 'application/json',
        type : 'POST',
        success: function(data){
            location = location.pathname.replace("configure","deploy")
        }
    });
}