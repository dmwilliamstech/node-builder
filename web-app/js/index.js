var newInstance = {nodes:{},applications:{}};

var configurations = {nodes: {},applications:{}};
var nodes;

$(document).ready(function() {
    $.getJSON(location.pathname.replace(/manifest.*/,'api/node'), function(json) {
        nodes = json
        $.each(nodes.data, function(ii, node){
            if(ii != 0){
                $('#newNodes').find('tbody:last').append("<tr>" +
                    "<td><h4>" + node.name + "</h4></td>" +
                    "<td>" +
                    '<div class="btn-toolbar">' +
                    '<a title="'+ node.name + '" name="node" id="node'+node.id+'" class="btn" onclick="handleAddNode(this)" title="Node added to manifest" >Add</a>' +
                    '<a href="#" id="infoNode'+node.id+'" class="btn" rel="popover" data-flavor="'+node.flavorId+'" data-content="'+node.description+'" data-original-title="'+node.name+'" ><i class="icon-info-sign icon-white"></i></a>' +
                    '</div>' +
                    '</td>' +
                    "</tr>");
                configurations.nodes[node.id] = node.properties.configurations
                $("[id^=info]").popover({offset: 10});
            }
            $.each(node.properties.applications, function(jj, application){
                $('#newApplications').find('tbody:last').append('<tr>' +
                    '<td><h4>' + application.name + '</h4></td>' +
                    '<td>' +
                    '    <div class="btn-toolbar">' +
                    '        <a title="'+ application.name + '" name="application" id="app'+application.id+'" class="btn" onclick="handleIncludeApplication(this)" title="Node added to manifest" >Include</a>' +
                    '        <a href="#" id="infoApp'+application.id+'" data-flavor="'+application.flavorId+'" class="btn" rel="popover" data-content="'+application.description+'" data-original-title="'+application.name+'"  ><i class="icon-info-sign icon-white"></i></a>' +
                    '    </div>' +
                    '</td>' +
                    '</tr>');
                configurations.applications[application.id] = application.properties.configurations
                $("[id^=info]").popover({offset: 10});
            });

        });


    });

    if(manifest.id){
        loadTabsFromManifest(manifest.instances[0].name)
        $("#manifestName").val(manifest.name)
    }
});

function handleAddNode(button){
    var id = button.id.replace(/node/, '')


    if($(button).hasClass("btn-success")){
        delete newInstance.nodes[id]
        $(button).removeClass("btn-success")
        $(button).html('Add')
    }else{
        $(button).addClass("btn-success")

        newInstance.nodes[id] = {id: id, configurations: configurations.nodes[id], name:button.title, flavorId: $('#infoNode' + id).data("flavor") }
        $(button).html('<i class="icon-ok icon-white"></i>Added')

        $.each(nodes.data, function(index, node){
            if(parseInt(id) === parseInt(node.id)){
                $.each(node.applications, function(blah, applicationId){
                    var appButton = $('#app' + applicationId)
                    appButton.id = 'app' + applicationId
                    appButton.title = appButton[0].title
                    if(!appButton.hasClass("btn-success"))
                        handleIncludeApplication(appButton);
                })
            }
        });
    }
}

function handleIncludeApplication(button){
    var id = button.id.replace(/app/, '')

    if($(button).hasClass("btn-success")){
        delete newInstance.applications[id]
        $(button).removeClass("btn-success")
        $(button).html("Include")
    }else{
        $(button).addClass("btn-success")
        newInstance.applications[id] = {id: id, configurations: configurations.applications[id], name:button.title, flavorId: $('#infoApp' + id).data("flavor") }
        $(button).html('<i class="icon-ok icon-white"></i>Included')
    }
}

function validateManifest(name){
    var alert = ""
    if(name == null || name.length == 0){
        alert += "Don't you want to name your new creation?<br>"
    }

    if(manifest.instances.length == 0){
        alert += "Not much point in proceeding without an instance<br>"
    }

    return alert
}

function handleConfigure(button, deploy){
    //post manifest and forward to configure screen
    var name = $("#manifestName").val()
    var alert = validateManifest(name)
    if(alert.length > 0){
        $("#alert").html('<div class="alert alert-error">'+alert+'</div>')
        return
    }


    manifest.name = name

    var path = location.pathname.replace(/show.*/, (manifest.id ? "update/"+manifest.id : "create"))
    $.ajax(path, {
        data : JSON.stringify(manifest),
        contentType : 'application/json',
        type : 'POST',
        success: function(data){
            if(deploy)
                location = location.pathname.replace(/show.*/,"deploy/") + data.id
            else
                $("#alert").html('<div class="alert alert-success">'+"Successfully saved manifest " + name + '</div>')
        }
    });
}

function handleSaveNewInstance(button){

    $('.modal-body').animate({ scrollTop:0}, 'fast')
    var name = $("#newInstanceName").val()
    if(name == null || name == ""){
        $("#newModalAlert").html('<div class="alert alert-error">Please provide a Name for the Instance</div>')

        return
    }else{
        newInstance.name = name
        newInstance.flavorId = getFlavorIdForInstance(newInstance)
        if(newInstance.id > 0){
            manifest.instances[newInstance.id - 1] = newInstance
        }else{
            manifest.instances.push(newInstance)
            newInstance.id = manifest.instances.length
        }
        resetSaveNewInstance();
        loadTabsFromManifest(name);
        $('#newModal').modal('hide');
    }
}

function getFlavorIdForInstance(instance){
    var flavorId = 1
    $.each(instance.nodes, function(ii, node){
        if(node.flavorId > flavorId)
            flavorId = node.flavorId
    })

    $.each(instance.applications, function(ii, application){
        if(application.flavorId > flavorId)
            flavorId = application.flavorId
    })

    return flavorId
}

function getFlavorName(flavorId){
    var name = "Unknown Flavor"
    $.each(flavors, function(ii, flavor){
        if(flavorId == flavor.flavorId)
            name = flavor.name
    })
    return name
}

function loadTabsFromManifest(active){
    $("ul.nav-tabs").html(
     '<li >' +
        '<a href="#newModal" data-toggle="modal" title="Add an Instance"><strong><i class="icon-plus-sign"></i> New Instance</strong></a>' +
    '</li>');
    $("div.tab-content").html('');
    $.each(manifest.instances, function(index, instance){
        var flavorName = getFlavorName(instance.flavorId)
        $("ul.nav-tabs").prepend('<li class="'+(active == instance.name ? "active": "")+'"><a href="#'+instance.name.replace(/\s/g,"")+'" data-toggle="tab" title="">'+instance.name+'</a></li>');
        var tabHtml = '<a class="pull-right" href="#" onclick="handleInstanceEdit('+index+')"><i class="icon-pencil"></i></a>' + '<h3>'+instance.name+' ('+flavorName+')</h3>' +
            '<div class="">' +
                '<h3>Suites</h3>' +
                '<table class="table table-bordered">' +
                '<thead>' +
                        '<tr>' +
                            '<th>' +
                                'Name' +
                            '</th>' +
            '<th style="width: 20%">' +
            'Actions' +
            '</th>' +
                        '</tr>' +
                    '</thead>' +
                    '<tbody>'
        $.each(instance.nodes, function(jndex, node){
            tabHtml += '<tr><td id="'+ index +'_'+ jndex +'nodes">' + node.name +
                '</td><td><a href="#" onclick="handleNodeEdit('+index+','+jndex+')"><i class="icon-pencil"></i></a></td></tr>'

        });


        tabHtml +=       '</tbody>' +
                '</table>' +
            '</div>'

        tabHtml +=
            '<div class="">' +
            '<h3>Applications</h3>' +
            '<table class="table table-bordered">' +
            '<thead>' +
            '<tr>' +
            '<th>' +
            'Name' +
            '</th>' +
                '<th style="width: 20%">' +
                'Actions' +
                '</th>' +
            '</tr>' +
            '</thead>' +
            '<tbody>'
        $.each(instance.applications, function(jndex, application){
            tabHtml += '<tr><td id="'+ index +'_'+ jndex +'applications">' + application.name +
            '</td><td><a href="#" onclick="handleApplicationEdit('+index+','+jndex+')"><i class="icon-pencil"></i></a></td></tr>'
        });


        tabHtml +=       '</tbody>' +
            '</table>' +
            '</div>'

        $("div.tab-content").prepend('<div class="tab-pane '+(active == instance.name ? "active": "")+'" id="'+instance.name.replace(/\s/g,"")+'">'+
            tabHtml
            +'</div>')
    });
}

function handleInstanceEdit(instanceIndex){
    var instance = manifest.instances[instanceIndex]
    $.each(instance.nodes, function(ii, node){
        $('#node' + node.id).addClass("btn-success")
        $('#node' + node.id).html('<i class="icon-ok icon-white"></i>Added')
    })

    $.each(instance.applications, function(ii, application){
        $('#app' + application.id).addClass("btn-success")
        $('#app' + application.id).html('<i class="icon-ok icon-white"></i>Included')
    })
    $('#newInstanceName').val(instance.name)
    newInstance = instance
    $('#newModal').modal('show');
}

function handleNodeEdit(instanceIndex, nodeIndex){
    addConfigurations(instanceIndex, nodeIndex, manifest.instances[instanceIndex].nodes[nodeIndex], 'node')
}

function handleApplicationEdit(instanceIndex, applicationIndex){
    console.log(instanceIndex + " " + applicationIndex)
    addConfigurations(instanceIndex, applicationIndex, manifest.instances[instanceIndex].applications[applicationIndex], 'application')
}

function addConfigurations(instanceIndex, index, object, type){
    console.log($('#'+instanceIndex + index + type + 's'))
    if($('#'+instanceIndex + index + type +'s').length > 0)
        return
    var html = '<hr id="'+instanceIndex + index + type+'s">'

    $.each(object.configurations, function(mm, configuration){

        html += '<div class="control-group">' +
            '<label class="control-label" for="'+ mm +'">'+ configuration.name +'</label>' +
            '<div class="controls">' +
            '<input type="text" value="'+ configuration.value + '" class="input-xlarge" id="'+ mm +'"  name="'+ instanceIndex + '_'+ index +'" onchange="handleInputChange(this, \''+type+'s\')">'
        if(configuration.description)
            html += '<p class="help-block">' +configuration.description+ '</p>'
        html += '</div>' +
            '</div>';
    });

    $('#' + instanceIndex + '_' + index + type + 's').append(
        html
    );
}

function handleInputChange(input, type){
    var instanceId = $(input)[0].name.split("_")[0]
    var inputId = $(input)[0].name.split("_")[1]
    manifest.instances[instanceId][type][inputId].configurations[$(input)[0].id].value = $(input).val()
}

function handleCloseNewInstance(button){
    $('.modal-body').animate({ scrollTop:0}, 'fast')
    resetSaveNewInstance()
    $('#newModal').modal('hide');
}

function resetSaveNewInstance(){
    $("ul.nav-tabs li").removeClass("active")
    $("#newModalAlert").html('')
    $(".btn").removeClass("btn-success")
    $("a[name='node']").html("Add")
    $("a[name='application']").html("Include")
    $("#newInstanceName").val("")
    newInstance = {nodes:{},applications:{}};
}