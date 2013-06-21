var newInstance = {nodes:{},applications:{}};
var manifest = {instances: []};
var configurations = {nodes: {},applications:{}};
var nodes;

$(document).ready(function() {
    $.getJSON('api/node', function(json) {
        nodes = json
        $.each(nodes.data, function(ii, node){
            if(ii != 0){
                $('#newNodes').find('tbody:last').append("<tr>" +
                    "<td><h4>" + node.name + "</h4></td>" +
                    "<td>" +
                    '<div class="btn-toolbar">' +
                    '<a title="'+ node.name + '" name="node" id="'+node.id+'" class="btn" onclick="handleAddNode(this)" title="Node added to manifest" >Add</a>' +
                    '<a href="#" id="infoNode'+node.id+'" class="btn" rel="popover" data-content="'+node.description+'" data-original-title="'+node.name+'" ><i class="icon-info-sign icon-white"></i></a>' +
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
                    '        <a title="'+ application.name + '" name="application" id="'+application.id+'" class="btn" onclick="handleIncludeApplication(this)" title="Node added to manifest" >Include</a>' +
                    '        <a href="#" id="infoApp'+application.id+'" class="btn" rel="popover" data-content="'+application.description+'" data-original-title="'+application.name+'"  ><i class="icon-info-sign icon-white"></i></a>' +
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
        delete newInstance.nodes[button.id]
        $(button).removeClass("btn-success")
        $(button).html('Add')
    }else{
        $(button).addClass("btn-success")
        newInstance.nodes[button.id] = {configurations: configurations.nodes[button.id], name:button.title }
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
        delete newInstance.applications[button.id]
        $(button).removeClass("btn-success")
        $(button).html("Include")
    }else{
        $(button).addClass("btn-success")
        newInstance.applications[button.id] = {configurations: configurations.applications[button.id], name:button.title }
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

function handleConfigure(button){
    //post manifest and forward to configure screen
    var name = $("#manifestName").val()
    var alert = validateManifest(name)
    if(alert.length > 0){
        $("#alert").html('<div class="alert alert-error">'+alert+'</div>')
        return
    }


    manifest.name = name

    $.ajax("manifest/create", {
        data : JSON.stringify(manifest),
        contentType : 'application/json',
        type : 'POST',
        success: function(data){
            location = "deploy/" + data.id
        }
    });
}

function handleSaveNewInstance(button){
    console.log("save")
    $('.modal-body').animate({ scrollTop:0}, 'fast')
    var name = $("#newInstanceName").val()
    if(name == null || name == ""){
        $("#newModalAlert").html('<div class="alert alert-error">Please provide a Name for the Instance</div>')

        return
    }else{
        newInstance.name = name
        manifest.instances.push(newInstance)
        resetSaveNewInstance();
        loadTabsFromManifest(name);
        $('#newModal').modal('hide');
        console.log(manifest)
    }
}

function loadTabsFromManifest(active){
    $("ul.nav-tabs").html(
     '<li >' +
        '<a href="#newModal" data-toggle="modal" title="Add an Instance"><strong>+ New Instance</strong></a>' +
    '</li>');
    $("div.tab-content").html('');
    $.each(manifest.instances, function(index, instance){
        $("ul.nav-tabs").prepend('<li class="'+(active == instance.name ? "active": "")+'"><a href="#'+instance.name.replace(/\s/g,"")+'" data-toggle="tab" title="">'+instance.name+'</a></li>');
        var tabHtml = '<h3>'+instance.name+'</h3>' +
            '<div class="">' +
                '<h3>Suites</h3>' +
                '<table class="table table-bordered">' +
                '<thead>' +
                        '<tr>' +
                            '<th>' +
                                'Name' +
                            '</th>' +
                        '</tr>' +
                    '</thead>' +
                    '<tbody>'
        $.each(instance.nodes, function(jndex, node){
            tabHtml += '<tr><td>' + node.name
                '<td></tr>'
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
                '<th>' +
                'Actions' +
                '</th>' +
            '</tr>' +
            '</thead>' +
            '<tbody>'
        $.each(instance.applications, function(jndex, application){
            tabHtml += '<tr><td id="'+ index +'_'+ jndex +'">' + application.name +
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

function handleApplicationEdit(instanceIndex, applicationIndex){
    console.log(manifest.instances[instanceIndex].applications[applicationIndex])

    console.log(instanceIndex)
    console.log(applicationIndex)
    addConfigurations(instanceIndex, applicationIndex, manifest.instances[instanceIndex].applications[applicationIndex], 'application')
}

function addConfigurations(instanceIndex, applicationIndex, object, type){
    if($('#'+instanceIndex + applicationIndex + type).length > 0)
        return
    var html = '<hr id="'+instanceIndex + applicationIndex + type+'">'

    $.each(object.configurations, function(mm, configuration){

        html += '<div class="control-group">' +
            '<label class="control-label" for="'+ mm +'">'+ configuration.name +'</label>' +
            '<div class="controls">' +
            '<input type="text" value="'+ configuration.value + '" class="input-xlarge" id="'+ mm +'"  name="'+ instanceIndex + '_'+ applicationIndex +'" onchange="handleInputChange(this, \''+type+'s\')">'
        if(configuration.description)
            html += '<p class="help-block">' +configuration.description+ '</p>'
        html += '</div>' +
            '</div>';
    });

    $('#' + instanceIndex + '_' + applicationIndex).append(
        html
    );
}

function handleInputChange(input, type){
    var instanceId = $(input)[0].name.split("_")[0]
    var inputId = $(input)[0].name.split("_")[1]
    console.log(manifest)
    console.log(instanceId)
    console.log(inputId)
    manifest.instances[instanceId][type][inputId].configurations[$(input)[0].id].value = $(input).val()
}

function handleCloseNewInstance(button){
    console.log("close")
    $('.modal-body').animate({ scrollTop:0}, 'fast')
    resetSaveNewInstance()
    $('#newModal').modal('hide');
}

function resetSaveNewInstance(){
    $("li").removeClass("active")
    $("#newModalAlert").html('')
    $(".btn").removeClass("btn-success")
    $("a[name='node']").html("Add")
    $("a[name='application']").html("Include")
    $("#newInstanceName").val("")
    newInstance = {nodes:{},applications:{}};
}

