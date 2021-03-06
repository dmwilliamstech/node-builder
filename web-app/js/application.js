

$(document).ready(function() {
//fix for bootstrap
var btn = $("a.btn-navbar")
if(btn.attr("href") == "")
    btn.attr("href", "#")

})

// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function(from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};


/*
 * --------------------------------------------------------------------
 * jQuery-Plugin - $.download - allows for simple get/post requests for files
 * by Scott Jehl, scott@filamentgroup.com
 * http://www.filamentgroup.com
 * reference article: http://www.filamentgroup.com/lab/jquery_plugin_for_requesting_ajax_like_file_downloads/
 * Copyright (c) 2008 Filament Group, Inc
 * Dual licensed under the MIT (filamentgroup.com/examples/mit-license.txt) and GPL (filamentgroup.com/examples/gpl-license.txt) licenses.
 * --------------------------------------------------------------------
 */
jQuery.download = function(url, data, method){
    //url and data options required
    if( url && data ){
        //data can be string of parameters or array/object
        data = typeof data == 'string' ? data : jQuery.param(data);
        var form = jQuery('<form action="'+ url +'" method="'+ (method||'post') +'"></form>');
        //split params into form inputs
        jQuery.each(data.split('&'), function(){
            var pair = this.split('=');
            form.append($('<input type="hidden" />').attr('name', pair[0]).attr('value', pair[1]));
        });
        //send request
        form.appendTo('body').submit().remove();
    };
};



