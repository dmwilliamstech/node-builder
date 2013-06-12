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
    data.name = $('#nameEdit').val()
    data.hostname = $('#hostnameEdit').val()
    data.username = $('#usernameEdit').val()
    data.privateKey = $('#privateKeyEdit').val()
    data.remotePath = $('#remotePathEdit').val()

    var path = ""
    var type = ""
    if($('#isEdit').val() == "true"){
        path = location.pathname.replace(/deploy.*/,"api/master/" + $('#idEdit').val())
        type = "PUT"
    }else{
        path = location.pathname.replace(/deploy.*/,"api/master")
        type = "POST"
    }

    $.ajax({
        type: type,
        url: path,
        data: JSON.stringify({data:data}),
        contentType: 'application/json',
        dataType: 'json',
        processData: false,
        success: function(response) {
            response.data.properties.name
            addMasterToDropdown(response.data)
            populateMaster(response)
            $('#modal').modal('hide')
        },
        error: function(response) {
            console.log(response)
        }
    })
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

    $('#idEdit').val(master.data.id)
    $('#nameEdit').val(master.data.name)
    $('#hostnameEdit').val(master.data.hostname)
    $('#usernameEdit').val(master.data.username)
    $('#privateKeyEdit').val(master.data.privateKey)
    $('#remotePathEdit').val(master.data.remotePath)


    $('#upload').removeClass('disabled')
    $("#edit").removeClass("disabled")
}

function addMasterToDropdown(master){
    if($('.dropdown-menu').find("#" + master.id).length == 0)
        $('.dropdown-menu').append('<li><a onclick="handleMasterSelect(this)"  id="' + master.id + '" >' + master.properties.name + '</a></li>')
    else{
        $('.dropdown-menu').find("#" + master.id)[0].innerHTML = master.properties.name
    }
}

function handleDownload(button){

    $.download(manifest.id + "/download/"+manifest.instanceName+".pp", 'fileName=' + '');

}

function handleShowModal(button) {
    var isEdit = button != undefined
    $('#isEdit').val(isEdit)
    if(!isEdit){
        $('#nameEdit').val("")
        $('#hostnameEdit').val("")
        $('#usernameEdit').val("")
        $('#privateKeyEdit').val("")
        $('#remotePathEdit').val("")
    }
    $('#modal').modal('show')
}

function handleUpload(button){
    $.post(location.pathname + '/upload/' + $('#id.uneditable-input').val(), {},
        function(response){
            $("#navbar").append('<div class="alert alert-info">File uploaded successfully to ' + response.name + '</div>')
        },
        "json"
    );
}