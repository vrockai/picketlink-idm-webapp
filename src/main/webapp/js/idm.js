var handlerGroupSel = function(name, type) {};

var paneUserAjax = "#MyContentArea";
var paneAttAjax = "#MyAttContentArea";
var paneGroupAjax = "#group-content-area";
var paneGroupSelAjax = "#MyGroupSelectContentArea";
var paneRoletypeAjax = "#MyRoletypeContentArea";
var paneRoleAjax = "#MyRoleContentArea";
var paneTabs = "#tabs";

var paneGroupBreadcrums = "#idm-group-breadcrumbs";

var offsetCookieRt = "idmRtOffset";
var offsetCookieUser = "idmUserOffset";
var offsetCookieAtt = "idmAttOffset";
var offsetCookieGroup = "idmGroupOffset";
var offsetCookieGroupSel = "idmGroupOffsetSel";
var offsetCookieRole = "idmRoleOffset";

var filterCookieRt = "idmRtOffset";
var filterCookieUser = "idmUserOffset";
var filterCookieAtt = "idmAttOffset";
var filterCookieGroup = "idmGroupOffset";
var filterCookieRole = "idmRoleOffset";

var filterElementRt = "#idm-rt-reset";
var filterElementUser = "#idm-user-reset";
var filterElementAtt = "#idm-att-reset";
var filterElementGroup = "#idm-group-reset";
var filterElementRole = "#idm-role-reset";

var groupParentId = "groupParent";
var groupTypeId = "groupType";

var urlRoot = "/jboss-idm-servlet/";
var urlIdm = urlRoot+"idm";
var urlUser = urlRoot+"user";
var urlAtt = urlRoot+"attribute";
var urlGroup = urlRoot+"group";
var urlRole = urlRoot+"role";
var urlRoletype = urlRoot+"roletype";
var urlPhoto = urlRoot+"photo"

var myMessages = ['note-info','note-warning','note-error','note-success']; // define the messages types

function abstractPaginationHandler(new_page_id, pagination_container, paginatorUrl, offsetCookie, paneAjax){

    if (new_page_id == null){
        new_page_id = $.cookie(offsetCookie);
        if (new_page_id == null){
            new_page_id = 0;
        } else if (new_page_id == ""){
            new_page_id = 0;
        }
    }

    $.cookie(offsetCookie, new_page_id);

    var offsetParam = paginatorUrl.indexOf("?") != -1 ? "&uO=" : "?uO=";

    loadByAjax(paneAjax, paginatorUrl+offsetParam+new_page_id);

    return false;
}

function hideAllMessages()
{
    var messagesHeights = new Array(); // this array will store height for each

    for (i=0; i<myMessages.length; i++)
    {
        messagesHeights[i] = $('.' + myMessages[i]).outerHeight();
        $('.' + myMessages[i]).css('top', -messagesHeights[i]); //move element outside viewport
    }
}

function setCookieFilter(cookieName, cookieVal, element){
    $.cookie(cookieName, cookieVal);

    if (cookieVal==""){
        $(element).parent().hide();
    } else {
        $(element).parent().show();
        $(element).val(cookieVal);
    }
}

function showMessage(type, message) {
    $('.'+type).children("p").html(message);
    $('.'+type).animate({
        top:"0"
    }, 500);
    setTimeout("$('."+type+"').animate({top: -$('."+type+"').outerHeight()}, 500);",3000);
}

function initCookie(name, value){
    $.cookie(name, value);
    return $.cookie(name);
}

function createPaginator(url, pane, handler){
    
    $(pane).children().remove();
    $(pane).append('<div class="p1 pagi"></div>');
    $(pane).append('<div class="conajax"><span class="idm-ajax-load" ></span></div>');
    $(pane).append('<div class="con"></div>');
    $(pane).append('<div class="p2 pagi"></div>');
    
    var pagiUrl = url.indexOf("?") != -1 ? url+"&a=5" : url+"?a=5";

    $.get(pagiUrl, function(data) {
        var elementCount = data;
        $(pane+" div.p1, "+pane+" div.p2").pagination({
            maxentries: elementCount,
            contentPane: pane,
            items_per_page:5,
            callback:handler
        });
        handler(null,null);
    });
}

function abstractAjaxAction(actionUrl, successMessage, errorMessage, browseUrl, browsePane, browseHandler, isRefresh){
    $.get(actionUrl, function(data) {
        if (data == "1"){
            showMessage("note-success", successMessage);
            if (isRefresh){
                createPaginator(browseUrl, browsePane, browseHandler);
            }
        } else {
            showMessage("note-error", errorMessage+"<br/>Error message: "+data);
        }
    });
}

function ajaxAction(actionUrl, successMessage, errorMessage){
    abstractAjaxAction(actionUrl, successMessage, errorMessage, null, null, null, false);
}

function ajaxActionWithRefresh(actionUrl, successMessage, errorMessage, browseUrl, browsePane, browseHandler){
    abstractAjaxAction(actionUrl, successMessage, errorMessage, browseUrl, browsePane, browseHandler, true);
}

function loadByAjax(ajaxPane, url){
    $(ajaxPane + " div.conajax").show();
    $(ajaxPane + " div.con").hide();
    $.get(url, function(data){
        $(ajaxPane + " div.con").html(data);
        $(ajaxPane + " div.conajax").hide();
        $(ajaxPane + " div.con").fadeIn();
    });
}

$(document).ready(function() {
    hideAllMessages();

    $('.note-message').click(function(){
        $(this).animate({
            top: -$(this).outerHeight()
            }, 500);
    });
});
