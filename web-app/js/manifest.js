$(document).on("click", ".icon-remove-sign", function () {
console.log($(this).data("type"))
    if($(this).data("type") == "manifest"){
        var manifestId = $(this).data('manifest-id');
        var manifestName = $(this).data('manifest-name');
        $(".modal-body #deleteManifestConfirm").html( "Are you sure you want to delete <i>" + manifestName + "</i>?" );
        $(".modal-footer .btn-primary").data("manifest-id", manifestId)
    }else if($(this).data("type") == "deployment"){

        var deploymentId = $(this).data('deployment-id');
        var manifestId = $(this).data('manifest-id');
        var manifestName = $(this).data('manifest-name');
        $(".modal-body #deleteDeploymentConfirm").html( "Are you sure you want to delete deployment "+deploymentId+" for <i>" + manifestName + "</i>?" );
        $("#deleteDeploymentButton").data("manifest-id", manifestId)
        $("#deleteDeploymentButton").data("deployment-id", deploymentId)
        $("#deleteDeploymentButton").data("manifest-name", manifestName)
    }
});

function handleUndeploy(button){
    $('#deleteDeploymentModal').modal('hide');
    var manifestId = $(button).data('manifest-id')
    var manifestName = $(button).data('manifest-name')
    var deploymentId = $(button).data('deployment-id')
    var path = "undeploy/"+manifestId+"/deployment/" + deploymentId
    $("#alert").html('<div class="alert alert-info">Removing deployment <i>'+manifestName+' ' +deploymentId+'</i> ...</div>')
    $.post(path, {},
        function(data){
            if(data.error){
                $("#alert").html('<div class="alert alert-error">Failed to remove deployment '+data.error.message+'</div>')
            }else{
                $("#deployment" + deploymentId).html('')
                $("#alert").html('<div class="alert alert-info">Removed deployment <i>'+manifestName+' ' +deploymentId+'</i></div>')
            }
        }
    ).error(function(){
            $("#alert").html('<div class="alert alert-error">Failed to remove deployment</div>')
    });
}

function handleDeleteManifest(button){
    var manifestId = $(button).data("manifest-id")
    var path = location.pathname.replace(/list.*/, "delete/"+manifestId)
    $.ajax(path, {
        contentType : 'application/json',
        type : 'DELETE',
        success: function(data){
            $('#deleteModal').modal('hide');
            location.reload();
        }
    });
}