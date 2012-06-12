<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Picketlink IDM webapp</title>
        <LINK REL=StyleSheet HREF="${pageContext.request.contextPath}/css/idm-main.css" TYPE="text/css" MEDIA=screen>
        <LINK REL=StyleSheet HREF="${pageContext.request.contextPath}/css/noty.css" TYPE="text/css" MEDIA=screen>
        <LINK REL=StyleSheet HREF="${pageContext.request.contextPath}/css/ui-lightness/jquery-ui-1.8.18.custom.css" TYPE="text/css" MEDIA=screen>
        <link rel="icon" href="${pageContext.request.contextPath}/img/picketlink_icon_16x.png" type="image/PNG">
        <script src="${pageContext.request.contextPath}/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/jquery.cookie.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/jquery.pagination.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/jquery.validator.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>

        <script>

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
                $('.'+type).animate({top:"0"}, 500);
                setTimeout("$('."+type+"').animate({top: -$('."+type+"').outerHeight()}, 500);",3000);
            }

            function initCookie(name, value){
                $.cookie(name, value);
                return $.cookie(name);
            }

            function createPaginator(url, pane, handler){
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
                abstractAjaxAction(actionUrl, successMessage, errorMessage, browseUrl, browsePane, browseHandler, false);
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

                $( "#tabs" ).tabs({ cookie: {expires: 1},fx: { opacity: 'toggle', duration: 'fast' } });

                $('div.idm-dialog-form').dialog({
                    autoOpen: false
                });

                hideAllMessages();

                $('.note-message').click(function(){
                    $(this).animate({top: -$(this).outerHeight()}, 500);
                });

                function handleGroupSelPaginationClick(new_page_id, pagination_container) {
                    var browseUrl = urlGroup+"?g=*&t=root_type&a=7";
                    return abstractPaginationHandler(new_page_id, pagination_container, browseUrl, offsetCookieGroupSel, paneGroupSelAjax);
                }

                createPaginator(urlGroup+"?g=*&t=root_type", paneGroupSelAjax, handleGroupSelPaginationClick);

                $("#dialog-group-select").dialog({
                    autoOpen: false,
                    modal: true,
                    width: 800,
                    dialogClass: "dialogWithDropShadow",
                    title: "Group selector"
                });

                $("#idm-grp-select").click(function() {
                    var name = $("#idm-group-sel-name").val();
                    var type = $("#idm-group-sel-type").val();

                    function handleGroupSelPaginationClick(new_page_id, pagination_container) {
                        var url = urlGroup+"?g="+name+"&t="+type+"&a=7&uO="+new_page_id;
                        loadByAjax(paneGroupSelAjax, url);
                        return false;
                    }

                    createPaginator(urlGroup+"?g="+name+"&t="+type, paneGroupSelAjax, handleGroupSelPaginationClick);
                });

                $("div.idm-filter-pane").toggle();
            });
        </script>
        <script src="http://jqueryui.com/themeroller/themeswitchertool/" type="text/javascript"></script>
        <script type="text/javascript">
            $(document).ready(function(){
                //     $('#switcher').themeswitcher();
            });
        </script>
        <style type="text/css">

        </style>
    </head>
    <body>
        <div class="note-info note-message">
            <h3>FYI, something just happened!</h3>
            <p>This is just an info notification message.</p>
        </div>

        <div class="note-error note-message">
            <h3>Ups, an error ocurred</h3>
            <p>This is just an error notification message.</p>
        </div>

        <div class="note-warning note-message">
            <h3>Wait, I must warn you!</h3>
            <p>This is just a warning notification message.</p>
        </div>

        <div class="note-success note-message">
            <h3>Congrats, you did it!</h3>
            <p>This is just a success notification message.</p>
        </div>
        <!--div id="switcher"></div-->

        <div id="dialog-group-select">
            <form class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">

                <label for="idm-group-sel-name">Group name:
                    <span class="small msg">Add group name:</span>
                </label>

                <input type="text" name="GroupName" value="*" id="idm-group-sel-name" class="ui-widget ui-state-default ui-corner-all idm" />

                <label for="idm-group-sel-type">Group type
                    <span class="small msg">Add group type:</span>
                </label>

                <input type="text" name="GroupType" value="*" id="idm-group-sel-type" class="ui-widget ui-state-default ui-corner-all idm" />

                <button id="idm-grp-select" type="button">Search</button>
            </form>

            <h2>Group list</h2>

            <div id="MyGroupSelectContentArea">
                <div class='p1 pagi'></div>
                <div class='conajax'><img src="${pageContext.request.contextPath}/img/ajax-loader.gif" class="idm-ajax-load" /></div>
                <div class='con'></div>
                <div class='p2 pagi'></div>
            </div>

        </div>

        <div class="main">
            <jsp:include page="search.jsp"/>
            <h1>Picketlink IDM GUI</h1>
            <div id="tabs">
                <ul>
                    <li><a href="#tabs-1">User</a></li>
                    <li><a href="#tabs-2">Group</a></li>
                    <li><a href="#tabs-3">Membership</a></li>
                    <li><a href="#tabs-4">Role types</a></li>
                </ul>
                <div id="tabs-1">
                    <jsp:include page="user.jsp"/>
                </div>
                <div id="tabs-2">
                    <jsp:include page="group.jsp"/>
                </div>
                <div id="tabs-3">
                    <jsp:include page="role.jsp"/>
                </div>
                <div id="tabs-4">
                    <jsp:include page="roletype.jsp"/>
                </div>
            </div>
        </div>
    </body>
</html>