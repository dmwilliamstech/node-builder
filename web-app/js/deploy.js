$(document).ready(function() {


    $('div.btn-group[data-toggle-name="image-select"]').each(function(){
        var group   = $(this);
        var form    = group.parents('form').eq(0);
        var name    = group.attr('data-toggle-name');

        var index = 0
        $('button', group).each(function(){
            var button = $(this);
            if(index == 0){
                manifest.imageId=button.val()
                manifest.imageName=button.data("name")
            }

            index++
            button.live('click', function(){
                manifest.imageId=$(this).val()
                manifest.imageName=$(this).data("name")
                button.addClass('active');
            });

        });
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
    if($(button).hasClass('disabled'))
        return
    var alert = validateManifest()
    if(alert.length == 0){
        $("#alert").html('<div class="alert alert-info">Beginning processing of manifest...</div>')
        $(button).addClass('disabled')
        $.ajax(location.pathname.replace(/deploy.*/,"update/") +manifest.id , {
            data : JSON.stringify(manifest),
            contentType : 'application/json',
            type : 'POST',
            success: function(data){
                $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
                    function(response){
                        if(response.name)
                            $("#alert").html('<div class="alert alert-info">Processing complete for ' + response.name + ' waiting for instance to start</div>')
                        else
                            $("#alert").html('<div class="alert alert-error">Failed to provision instance - '+response.error.message+'</div>')

                        $(button).removeClass('disabled')
                    },
                    "json"
                ).error(function(){
                    $("#alert").html('<div class="alert alert-error">Failed to provision instance</div>')
                    $(button).removeClass('disabled')
                });
            }
        });
    } else {
        $('#alert').html( '<div class="alert alert-info">'+alert+'</div>')
    }
}