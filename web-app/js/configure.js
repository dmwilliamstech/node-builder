$(document).ready(function() {
    $.each(manifest.instances, function(instanceId, instance){
        $.each(instance.nodes, function(id, node){
            addConfigurations(instanceId, id, node, "node");
        });

        $.each(instance.applications, function(id, application){
            addConfigurations(instanceId, id, application, "application");
        });
    });



});

function addConfigurations(instanceId, id, object, type){
    var html =
        '<tr id="'+instanceId +id + type+'">' +
        '<td>' + "<h4>" + object.name + "</h4>"
    $.each(object.configurations, function(mm, configuration){

        html += '<div class="control-group">' +
            '<label class="control-label" for="'+ mm +'">'+ configuration.name +'</label>' +
            '<div class="controls">' +
            '<input type="text" value="'+ configuration.value + '" class="input-xlarge" id="'+ mm +'"  name="'+ instanceId + '_'+ id +'" onchange="handleInputChange(this, \''+type+'s\')">'
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
    var instanceId = $(input)[0].name.split("_")[0]
    var inputId = $(input)[0].name.split("_")[1]
    console.log(manifest)
    console.log(instanceId)
    console.log(inputId)
    manifest.instances[instanceId][type][inputId].configurations[$(input)[0].id].value = $(input).val()
}



function handleDeploy(button){
    if(alert.length == 0){
        console.log(manifest)
        //post manifest and forward to configure screen
        $.ajax(location.pathname.replace(/configure.*/,"manifest/update/") +manifest.id, {
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



