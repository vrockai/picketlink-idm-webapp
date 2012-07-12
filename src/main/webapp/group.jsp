<%@page import="org.picketlink.idm.servlet.bean.UserBean"%>
<%@page import="org.picketlink.idm.servlet.processor.IdmProcessor"%>
<%@page import="org.picketlink.idm.api.Group"%>
<%@page import="org.picketlink.idm.api.Role"%>
<%@page import="java.util.List"%>
<%@page import="org.picketlink.idm.api.IdentitySearchCriteria"%>
<%@page import="org.picketlink.idm.api.User"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.picketlink.idm.api.IdentitySession"%>
<%@page import="org.picketlink.idm.impl.configuration.IdentityConfigurationImpl"%>
<%@page import="org.picketlink.idm.api.IdentitySessionFactory"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
    function handleGroupPaginationClick(new_page_id, pagination_container) {
        var gP = $.cookie(groupParentId);
        var gT = $.cookie(groupTypeId);    
        var urlGroupWithParam = urlGroup+"?p="+gP+"&t="+gT;        
        return abstractPaginationHandler(new_page_id, pagination_container, urlGroupWithParam, offsetCookieGroup, paneGroupAjax);
    }
    
    $(function() {
        var groupOffset = initCookie(offsetCookieGroup, 0);        
        var groupParent = initCookie(groupParentId, "");
        var groupType = initCookie(groupTypeId, "root_type");
        
        setCookieFilter(filterCookieGroup, "", filterElementGroup);
        
        $("#idm-group-reset").click(function() {            
            loadByAjax(paneGroupAjax, urlGroup);
        });
        
        $("button.idm-delete").button({ icons: { primary: "ui-icon-closethick" }});
        $("button.idm-open").button({ icons: { primary: "ui-icon-folder-collapsed" }});
                 
        $("#GroupName").validate({
            messageContainerInvalid: $("#GroupName").next(),
            validateFunction:function(){return true;}
        });          
        
        createPaginator(urlGroup, paneGroupAjax, handleGroupPaginationClick);
        
        $.get(urlGroup+'?a=6', function(data) {
            $(paneGroupBreadcrums).html(data);
        });
        
        $(".idm-grp-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var grpName = $("#GroupName").val();            
            $("#idm-grp-create-id").val(grpName);
            $( "#create-grp-dialog-form" ).dialog( "open" );
        });          
    
        $( "#create-grp-dialog-form" ).dialog({
            autoOpen: false,
            width: 450,
            modal: true,
            buttons: {
                "Create new group": function() {
                    var gId = $("#idm-grp-create-id").val();
                    var gType = $("#idm-grp-create-type").val();
                    var args = "gId="+gId+"&t="+gType;
                    var groupActionUrl = urlGroup+"?a=1&"+args;
                    ajaxActionWithRefresh(groupActionUrl, "Group add succesfull!", "Unable to create group.", urlGroup, paneGroupAjax, handleGroupPaginationClick);                    
                    $(this).dialog( "close" );
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
                
            },
            open: function() {
                $('.ui-dialog-buttonpane').
                    find('button:first-child').button({
                    icons: {
                        primary: 'ui-folder-open'
                    }
                }).next().button({
                    icons: {
                        primary: 'ui-icon-cancel'
                    }
                });
            }
        });
    });
</script>

<div id="create-grp-dialog-form" title="Create new group">    
    <form class="idm-basic-form">
        <label class="ui-widget" for="uId">Group name:
            <span class="small">Add your group ID</span>
        </label><input type="text" name="gId" id="idm-grp-create-id" class="ui-widget ui-state-default ui-corner-all idm"/>
        <label class="ui-widget">Group type:
            <span class="small">Add the group type</span>
        </label><input type="text" value="root_type" name="gType" id="idm-grp-create-type" class="ui-widget ui-state-default ui-corner-all idm"/>          
    </form>
</div>

<form class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">
    <label for="userId">Group name:
        <span class="small msg">Add group name:</span>        
    </label><input type="text" name="GroupName" value="defaultGroup" id="GroupName" class="ui-widget ui-state-default ui-corner-all idm" />   
    <div class="error">tu bude validation error</div>   
    <button class="idm-grp-add" type="button">Add new group...</button>
</form>

<h2 class="idm-list-header">Group list</h2>
<div class="idm-filter-pane">(filter: 
    <input disabled class="ui-widget ui-state-disabled ui-corner-all idm" value=""/>
    , to see all groups click <a href="#/" id="idm-group-reset">here</a>)
</div>

<div id="idm-group-breadcrumbs"></div>

<div id="group-content-area"></div>