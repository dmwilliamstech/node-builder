$(document).ready(function() {

    var path = location.pathname.replace(/deploy.*/,"api/master")
    $.getJSON(path, function(masters) {

        $.each(masters.data, function(index, master){
            addMasterToDropdown(master)
        });
        $('.dropdown-toggle').dropdown()
    });

    $("input.uneditable-input").prop('disabled', true)
    $("textarea.uneditable-textarea").attr("disabled", "disabled")
});


function handleSaveMaster(button){
    //post to api/master
    var data = {}
    data.name = $('#name').val()
    data.hostname = $('#hostname').val()
    data.username = $('#username').val()
    data.privateKey = $('#privateKey').val()
    data.remotePath = $('#remotePath').val()


    var path = location.pathname.replace(/deploy.*/,"api/master")
    $.post(path, data,
        function(response){
            response.data.properties.name
            addMasterToDropdown(response.data)
            populateMaster(response)
            $('#modal').modal('hide')
        },
        "json"
    );
}

function handleMasterSelect(button){
    var path = location.pathname.replace(/deploy.*/,"api/master/" + $(button)[0].id)

    $.getJSON(path, function(master) {
        populateMaster(master)
    });

}

function populateMaster(master){
    $('#id.uneditable-input').val(master.data.id)
    $('#name.uneditable-input').val(master.data.name)
    $('#hostname.uneditable-input').val(master.data.hostname)
    $('#username.uneditable-input').val(master.data.username)
    $('#privateKey.uneditable-textarea').val(master.data.privateKey)
    $('#remotePath.uneditable-input').val(master.data.remotePath)
    $('#upload').removeClass('disabled')
}

function addMasterToDropdown(master){
    $('.dropdown-menu').append('<li><a onclick="handleMasterSelect(this)"  id="' + master.id + '" >' + master.properties.name + '</a></li>')
}

function handleDownload(button){

    $.download(manifest.id + "/download/site.pp", 'fileName=' + '');

}

function handleUpload(button){
    $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
        function(response){
            $("#navbar").append('<div class="alert alert-info">File uploaded successfully to ' + response.name + '</div>')
        },
        "json"
    );
}