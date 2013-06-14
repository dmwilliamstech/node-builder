$(document).ready(function() {

});



function handleDownload(button){

    $.download(manifest.id + "/download/"+manifest.instanceName+".pp", 'fileName=' + '');

}


function handleUpload(button){
    $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
        function(response){
            if($(".alert-info").length == 0)
                $("#navbar").append('<div class="alert alert-info">File uploaded successfully to ' + response.name + '</div>')
        },
        "json"
    ).error(function(){
        if($(".alert-error").length == 0)
            $("#navbar").append('<div class="alert alert-error">File failed to upload to master</div>')
    });
}