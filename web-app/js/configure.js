$(document).ready(function() {
    $.each(manifest.nodes, function(id, node){
        addConfigurations(id, node, "node");
    });

    $.each(manifest.applications, function(id, application){
        addConfigurations(id, application, "application");
    });

    if(manifest.imageName)
        $('#imageName')[0].innerHTML = manifest.imageName
    if(manifest.instanceName)
        $('#instanceName').val(manifest.instanceName)

    $('.dropdown-toggle').dropdown()
    var path = location.pathname.replace(/configure.*/,"api/image")

    $.getJSON(path, function(images) {
        var notFound = '<li><a title="No Images Found">Sorry no images found</a></li>'
        if(images && images.count > 0){

            $.each(images.data, function(index, image){
                var html = '<li><a onclick="handleImageSelect(this)"  id="' + image.imageId + '" >' + image.name + '</a></li>'
                $('.dropdown-menu').append(html)
            });

        }else{
            $('.dropdown-menu').append(notFound)
        }

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

function validateManifest(){
    var alert = ""
    if(!manifest.imageName){
        alert += "Please select an image <br>"
    }

    if(!manifest.instanceName){
        alert += "Please enter an instance name <br>"
    }

    return alert
}

function handleDeploy(button){
    var alert = validateManifest()


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
        $('#alert').append( '<div class="alert alert-info">'+alert+'</div>')
    }
}

function handleInstanceNameChange(input){
    manifest.instanceName = $(input).val()
}

function handleImageSelect(button){
    $('#imageName')[0].innerHTML = button.innerHTML

    manifest.imageId = $(button)[0].id
    manifest.imageName = $(button)[0].innerText

}