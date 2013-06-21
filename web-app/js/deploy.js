$(document).ready(function() {
    if(manifest.imageName)
        $('#imageName')[0].innerHTML = manifest.imageName
    if(manifest.instanceName)
        $('#instanceName').val(manifest.instanceName)

    $('.dropdown-toggle').dropdown()
    var path = location.pathname.replace(/deploy.*/,"api/image")

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



function handleDownload(button){

    $.download(manifest.id + "/download/"+manifest.instanceName+".pp", 'fileName=' + '');

}

function validateManifest(){
    var alert = ""
    if(!manifest.imageName){
        alert += "Please select an image <br>"
    }

    return alert
}


function handleUpload(button){
    var alert = validateManifest()
    if(alert.length == 0){
        $.ajax(location.pathname.replace(/deploy.*/,"manifest/update/") +manifest.id , {
            data : JSON.stringify(manifest),
            contentType : 'application/json',
            type : 'POST',
            success: function(data){
                $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
                    function(response){
                        $("#alert").html('<div class="alert alert-info">Provisioning from ' + response.name + ' waiting for instance to start</div>')
                    },
                    "json"
                ).error(function(){
                    $("#alert").html('<div class="alert alert-error">Failed to provision instance</div>')
                });
            }
        });
    } else {
        $('#alert').html( '<div class="alert alert-info">'+alert+'</div>')
    }
}

function handleInstanceNameChange(input){
    var name = $(input).val()
    $.each(instances, function(index, instance){
        if(name == instance.name){
            $("#alert").html('<div class="alert alert-error"><strong>'+name+'</strong> already in use, please choose a different name</div>')
            return
        }else{
            $("#alert").html('')
        }

    })
    manifest.instanceName = name
}

function handleImageSelect(button){
    $('#imageName')[0].innerHTML = button.innerHTML

    manifest.imageId = $(button)[0].id
    manifest.imageName = $(button)[0].innerText

}