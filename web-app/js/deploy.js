$(document).ready(function() {

});



function handleDownload(button){

    $.download(manifest.id + "/download/"+manifest.instanceName+".pp", 'fileName=' + '');

}


function handleUpload(button){
    $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
        function(response){
            $("#alert").html('<div class="alert alert-info">Provisioning from ' + response.name + ' waiting for instance to start</div>')
        },
        "json"
    ).error(function(){
        $("#alert").html('<div class="alert alert-error">Failed to provision instance</div>')
    });
}