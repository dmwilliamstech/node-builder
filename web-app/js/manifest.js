$(document).on("click", ".icon-remove-sign", function () {
    var manifestId = $(this).data('manifest-id');
    var manifestName = $(this).data('manifest-name');
    $(".modal-body #deleteManifestConfirm").html( "Are you sure you want to delete <i>" + manifestName + "</i>?" );
    $(".modal-footer .btn-primary").data("manifest-id", manifestId)
});


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