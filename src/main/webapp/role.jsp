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

<style>
    .ui-idm-header {
        width:96%;
        margin: 0px;
        padding: 15px;
    }
</style>

<script>
    $(function() {
        
        var roleOffset = initCookie(offsetCookieRole, 0);
                setCookieFilter(filterCookieRole, "", filterElementRole);
        $("#idm-role-reset").click(function() {            
            loadByAjax(paneRoleAjax, urlRole);
        });
        
        $("button.idm-delete").button({ icons: { primary: "ui-icon-closethick" }});
        
        function handleRolePaginationClick(new_page_id, pagination_container) {
            return abstractPaginationHandler(new_page_id, pagination_container, urlRole, offsetCookieRole, paneRoleAjax);
        }
        
        createPaginator(urlRole, paneRoleAjax, handleRolePaginationClick);
        
        $("#roleUserId").mousedown(function(){
           $(paneTabs).tabs('select', 0);
           $("button.idm-role-user-add").effect("highlight", 2000);
        });
        
        $("#roleGroupId").mousedown(function(){
           $(paneTabs).tabs('select', 1);
           $("button.idm-role-group-add").effect("highlight", 2000);
        });
        
        $("#roleRtId").mousedown(function(){
           $(paneTabs).tabs('select', 3);
           $("button.idm-role-rt-add").effect("highlight", 2000);
        });
        
        $("button.idm-role-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var rtId = $("#roleRtId").val();
            var userId = $("#roleUserId").val();
            var groupId = $("#roleGroupId").val();
            var args = "rtId="+rtId+"&uId="+userId+"&gId="+groupId;
            $.get(urlRole+"?a=1&"+args, function(data) {
                if (data == "1"){
                    showMessage("note-success", "Role add succesfull.");
                    loadByAjax(paneRoleAjax, urlRole+"?uO="+roleOffset);
                } else {
                    showMessage("note-error", "Unable to add role.<br/>Error message: "+data);    
                }
            });    
        });
        
        $( "#dialog-role-search-user" ).dialog({
            autoOpen: false,
            width: 450,
            modal: true,
            buttons: {
                "Create an account": function() {                    
                    $(this).dialog( "close" );
                },
                Cancel: function() {
                    $(this).dialog( "close" );
                }
            },
            close: function() {
                
            },
            open: function() {
             
            }
        });
    });
</script>

<div id="dialog-role-search-user" title="Find user">    
    <form class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">
        <label for="userId">Find user:
            <span class="small msg">Add user e-mail</span>        
        </label><input type="text" name="userId" value="*" id="roleFindUserId" class="ui-widget ui-state-default ui-corner-all idm" />           
        <button class="idm-user-add" type="button">Search</button>
    </form>
    <div id="paneRoleSearchUser">
        empty
    </div>
</div>

<form method="POST" action="associateRole" class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">
    <label for="roletypeId">Roletype:
        <span class="small msg">Add your group ID</span>
    </label>

    <select id="roleRtId" name="roletypeId" class="ui-widget ui-state-default ui-corner-all idm">
        <option value="default">select roletype</option>
        <c:forEach var="roletype" items="${roletypeList}">
            <option value="${roletype.name}">${roletype.name}</option>
        </c:forEach>
    </select>

    <label for="userId">User ID:
        <span class="small msg">Add your group ID</span>
    </label>

    <select id="roleUserId" name="userId" class="ui-widget ui-state-default ui-corner-all idm">
        <option value="default">select user</option>
        <!--
        <c:forEach var="user" items="${userPagiList}">
            <option value="${user.userId}">${user.userId}</option>
        </c:forEach>
        -->
    </select>

    <label for="groupName">Group name:
        <span class="small msg">Add your group ID</span>
    </label>

    <select id="roleGroupId" name="groupName" class="ui-widget ui-state-default ui-corner-all idm">
        <option value="default">select group</option>
        <c:forEach var="group" items="${groupList}">
            <option value="${group.name}">${group.name}</option>
        </c:forEach>
    </select>

    <button class="idm-role-add" type="button">Associate role</button>
</form>


<h2 class="idm-list-header">Role list</h2>
<div class="idm-filter-pane">(filter: 
    <input disabled class="ui-widget ui-state-disabled ui-corner-all idm" value=""/>
    , to see all roles click <a href="#/" id="idm-role-reset">here</a>)
</div>

<div id="MyRoleContentArea">
    <div class='p1 pagi'></div>
    <div class='conajax'><img src='${pageContext.request.contextPath}/img/ajax-loader.gif' class="idm-ajax-load" /></div>
    <div class='con'></div>
    <div class='p2 pagi'></div>
</div>