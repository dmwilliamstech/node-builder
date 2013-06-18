$(document).ready(function() {

});



function handleDownload(button){

    $.download(manifest.id + "/download/"+manifest.instanceName+".pp", 'fileName=' + '');

}


function handleUpload(button){
    $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
        function(response){
            if($(".alert-info").length == 0)
                $("#navbar").append('<div class="alert alert-info">Provisioning ' + response.name + ' waiting for instance to start</div>')
        },
        "json"
    ).error(function(){
        if($(".alert-error").length == 0)
            $("#navbar").append('<div class="alert alert-error">Failed to provision instance</div>')
    });
}