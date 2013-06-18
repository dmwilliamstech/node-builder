$(document).ready(function() {
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
            '<input type="text" value="'+ configuration.value + '" class="input-xlarge" id="'+ mm +'"  name="'+ id +'" onchange="handleInputChange(this, \''+type+'s\')">'
            if(configuration.description)
                html += '<p class="help-block">' +configuration.description+ '</p>'
            html += '</div>' +
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



    if(alert.length == 0){
        //post manifest and forward to configure screen
        $.ajax("update/" +manifest.id , {
            data : JSON.stringify(manifest),
            contentType : 'application/json',
            type : 'POST',
            success: function(data){
                location = location.pathname.replace("configure","deploy")
            }
        });
    } else {
        $('#alert').html( '<div class="alert alert-info">'+alert+'</div>')
    }
}



