var manifest = {nodes:{},applications:{}};
var configurations = {nodes: {},applications:{}};
var nodes;

//<a href="#" id="blob" class="btn large primary" rel="popover" data-content="And here's some amazing content. It's very engaging. right?" data-original-title="A title">hover for popover</a>

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
                    '<a href="#" id="infoNode'+node.id+'" class="btn btn-large" rel="popover" data-content="'+node.description+'" data-original-title="'+node.name+'" ><i class="icon-info-sign icon-white"></i></a>' +
                    '</div>' +
                    '</td>' +
                    "</tr>");
                configurations.nodes[node.id] = node.properties.configurations
                $("[id^=info]").popover({offset: 10});
            }
            $.each(node.properties.applications, function(jj, application){
                $('#applications').find('tbody:last').append('<tr>' +
                    '<td><h3>' + application.name + '</h3></td>' +
                    '<td>' +
                    '    <div class="btn-toolbar">' +
                    '        <a title="'+ application.name + '" name="application" id="'+application.id+'" class="btn btn-large" onclick="handleIncludeApplication(this)" title="Node added to manifest" >Include</a>' +
                    '        <a href="#" id="infoApp'+application.id+'" class="btn btn-large" rel="popover" data-content="'+application.description+'" data-original-title="'+application.name+'"  ><i class="icon-info-sign icon-white"></i></a>' +
                    '    </div>' +
                    '</td>' +
                    '</tr>');
                configurations.applications[application.id] = application.properties.configurations
                $("[id^=info]").popover({offset: 10});
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
    var name = $("#manifestName").val()
    if(name == null || name == ""){
        $("#alert").html('<div class="alert alert-error">Please provide a Name for the manifest</div>')
        return
    }
    manifest.name = name

    $.ajax("manifest/create", {
        data : JSON.stringify(manifest),
        contentType : 'application/json',
        type : 'POST',
        success: function(data){
            location = "configure/" + data.id
        }
    });
}