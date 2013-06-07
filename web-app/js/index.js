var manifest = {nodes:{},applications:{}};
var configurations = {nodes: {},applications:{}};
var nodes;

$(document).ready(function() {
    $.getJSON('api/node', function(json) {
        nodes = json
        $.each(nodes.data, function(ii, node){
            if(ii != 0){
                $('#nodes').find('tbody:last').append("<tr>" +
                    "<td><h3>" + node.name + "</h3></td>" +
                    "<td>" +
                    '<div class="btn-toolbar">' +
                    '<a title="'+ node.name + '" name="node" id="'+node.id+'" class="btn btn-large" onclick="handleAddNode(this)" title="Node added to manifest" >Add</a>' +
                    '<a class="btn btn-large" title="Info"><i class="icon-info-sign icon-white"></i></a>' +
                    '</div>' +
                    '</td>' +
                    "</tr>");
                configurations.nodes[node.id] = node.properties.configurations
            }
            $.each(node.properties.applications, function(jj, application){
                $('#applications').find('tbody:last').append('<tr>' +
                    '<td><h3>' + application.name + '</h3></td>' +
                    '<td>' +
                    '    <div class="btn-toolbar">' +
                    '        <a title="'+ application.name + '" name="application" id="'+application.id+'" class="btn btn-large" onclick="handleIncludeApplication(this)" title="Node added to manifest" >Include</a>' +
                    '        <a class="btn btn-large" title="Info"><i class="icon-info-sign icon-white"></i></a>' +
                    '    </div>' +
                    '</td>' +
                    '</tr>');
                configurations.applications[application.id] = application.properties.configurations
            });

        });
    });

});

function handleAddNode(button){
    if($(button).hasClass("btn-success")){
        delete manifest.nodes[button.id]
        $(button).removeClass("btn-success")
        $(button).html('Add')
    }else{
        $(button).addClass("btn-success")
        manifest.nodes[button.id] = {configurations: configurations.nodes[button.id], name:button.title }
        $(button).html('<i class="icon-ok icon-white"></i>Added')

        $.each(nodes.data, function(index, node){
            if(parseInt(button.id) === parseInt(node.id)){
                $.each(node.applications, function(blah, applicationId){
                    var appButton = $('a[name=application][id='+applicationId+'][title]')
                    appButton.id = applicationId
                    appButton.title = appButton[0].title
                    if(!appButton.hasClass("btn-success"))
                        handleIncludeApplication(appButton);
                })
            }
        });
    }
}

function handleIncludeApplication(button){
    if($(button).hasClass("btn-success")){
        delete manifest.applications[button.id]
        $(button).removeClass("btn-success")
        $(button).html("Include")
    }else{
        $(button).addClass("btn-success")
        manifest.applications[button.id] = {configurations: configurations.applications[button.id], name:button.title }
        $(button).html('<i class="icon-ok icon-white"></i>Included')
    }
}

function handleConfigure(button){
    //post manifest and forward to configure screen
    $.ajax("manifest/create", {
        data : JSON.stringify(manifest),
        contentType : 'application/json',
        type : 'POST',
        success: function(data){
            location = "configure/" + data.id
        }
    });
}