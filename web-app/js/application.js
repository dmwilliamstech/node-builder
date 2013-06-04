// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};


var manifest = {nodes:[],applications:[]};
var nodes;

$(document).ready(function() {
    $.getJSON('api/node', function(json) {
        nodes = json
        console.log(nodes)
        $.each(nodes.data, function(ii, node){
            if(ii != 0){
                $('#nodes').find('tbody:last').append("<tr>" +
                    "<td><h3>" + node.name + "</h3></td>" +
                    "<td>" +
                    '<div class="btn-toolbar">' +
                    '<a name="node" id="'+node.id+'" class="btn btn-large" onclick="handleAddNode(this)" title="Node added to manifest" >Add</a>' +
                    '<a class="btn btn-large" title="Info"><i class="icon-info-sign icon-white"></i></a>' +
                    '</div>' +
                    '</td>' +
                    "</tr>");

                $.each(node.properties.applications, function(jj, application){
                    console.log(application)
                    $('#applications').find('tbody:last').append('<tr>' +
                        '<td><h3>' + application.name + '</h3></td>' +
                        '<td>' +
                        '    <div class="btn-toolbar">' +
                        '        <a name="application" id="'+application.id+'" class="btn btn-large" onclick="handleIncludeApplication(this)" title="Node added to manifest" >Include</a>' +
                        '        <a class="btn btn-large" title="Info"><i class="icon-info-sign icon-white"></i></a>' +
                        '    </div>' +
                        '</td>' +
                        '</tr>');
                });
            }
        });
    });

});

function handleAddNode(button){
    if($(button).hasClass("btn-success")){
        manifest.nodes.remove(manifest.nodes.indexOf(button.id))
        $(button).removeClass("btn-success")
        $(button).html('Add')
    }else{
        $(button).addClass("btn-success")
        manifest.nodes.push(button.id)
        $(button).html('<i class="icon-ok icon-white"></i>Added')
        console.log(nodes.data)
        $.each(nodes.data, function(index, node){
            $.each(node.applications, function(blah, applicationId){
                handleIncludeApplication($('a[name=application][id='+applicationId+']'));
            })

        });
    }
    console.log(manifest)
}

function handleIncludeApplication(button){
    if($(button).hasClass("btn-success")){
        manifest.applications.remove(manifest.applications.indexOf(button.id))
        $(button).removeClass("btn-success")
        $(button).html("Include")
    }else{
        $(button).addClass("btn-success")
        manifest.applications.push(button.id)
        $(button).html('<i class="icon-ok icon-white"></i>Included')
    }
    console.log(manifest)
}



//<tr>
//    <td><h3>AIS Processor</h3></td>
//    <td>
//        <div class="btn-toolbar">
//            <a class="btn btn-success btn-large" href="" title="Node added to manifest"><i class="icon-ok icon-white"></i>Added</a>
//            <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//        </div>
//    </td>
//</tr>
//    <tr>
//    <td><h3>Image Processor/Viewer</h3></td>
//    <td>
//    <div class="btn-toolbar">
//        <a class="btn btn-large" href="" title="Add a node type"> Add </a>
//        <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//    </div>
//</td>
//</tr>


//<tr>
//    <td><h3>DDF</h3></td>
//    <td>
//        <div class="btn-toolbar">
//            <a class="btn btn-success btn-large" href="" title="Node added to manifest"><i class="icon-ok icon-white"></i>Included</a>
//            <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//        </div>
//    </td>
//</tr>
//    <tr>
//    <td><h3>Ozone</h3></td>
//    <td>
//    <div class="btn-toolbar">
//        <a class="btn btn-large" href="" title="Add a node type"> Include </a>
//        <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//    </div>
//</td>
//</tr>
//    <tr>
//        <td><h3>Ortellium</h3></td>
//        <td>
//            <div class="btn-toolbar">
//                <a class="btn btn-large" href="" title="Add a node type"> Include </a>
//                <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//            </div>
//        </td>
//    </tr>
//    <tr>
//        <td><h3>Stratum 5</h3></td>
//        <td>
//            <div class="btn-toolbar">
//                <a class="btn btn-large" href="" title="Add a node type"> Include </a>
//                <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//            </div>
//        </td>
//    </tr>
//    <tr>
//        <td><h3>Image Ingestion</h3></td>
//        <td>
//            <div class="btn-toolbar">
//                <a class="btn btn-large" href="" title="Add a node type"> Include </a>
//                <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//            </div>
//        </td>
//    </tr>
//    <tr>
//        <td><h3>AIS Ingestion</h3></td>
//        <td>
//            <div class="btn-toolbar">
//                <a class="btn btn-success btn-large" href="" title="Node added to manifest"><i class="icon-ok icon-white"></i>Included</a>
//                <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//            </div>
//        </td>
//    </tr>
//    <tr>
//        <td><h3>GEO Bus</h3></td>
//        <td>
//            <div class="btn-toolbar">
//                <a class="btn btn-large" href="" title="Add a node type"> Include </a>
//                <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//            </div>
//        </td>
//    </tr>
//    <tr>
//        <td><h3>Some Coalition Bundle</h3></td>
//        <td>
//            <div class="btn-toolbar">
//                <a class="btn btn-large" href="" title="Add a node type"> Include </a>
//                <a class="btn btn-large" href="" title="Info"><i class="icon-info-sign icon-white"></i></a>
//            </div>
//        </td>
//    </tr>