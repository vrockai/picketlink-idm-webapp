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
    $(function() {
        
        var roleOffset = $.cookie(offsetCookieRole);
       
        $("button.idm-role-delete").button({ icons: { primary: "ui-icon-closethick" }}).click(function() {            
            var roleRtId = $(this).siblings("form").children('input[name="rtId"]').val();
            var roleUserId = $(this).siblings("form").children('input[name="uId"]').val();
            var roleGroupId = $(this).siblings("form").children('input[name="gId"]').val();

            $("#roleDelUserId").html(roleUserId);
            $("#roleDelRtId").html(roleRtId);
            $("#roleDelGroupId").html(roleGroupId);

            $( "#dialog-role-delete" ).dialog("open");
        });

        $("span.idm-user-lnk").click(function(){
            var query = $(this).html();
            var url = urlUser+"?q="+query;
            loadByAjax(paneUserAjax, url);
            $(paneTabs).tabs('select', 0);
        });
        
        $("span.idm-rt-lnk").click(function(){
            var query = $(this).html();
            var url = urlRoletype+"?q="+query;
            loadByAjax(paneRoletypeAjax, url);
            $(paneTabs).tabs('select', 3);
        });
        
        $("span.idm-group-lnk").click(function(){
            var query = $(this).html();
            var gType = $(this).siblings("span.idm-groupType-lnk").html();
            var url = urlGroup+"?q="+query+"&t="+gType;
            loadByAjax(paneGroupAjax, url);
            $(paneTabs).tabs('select', 1);
        });

        $( "#dialog-role-delete" ).dialog({
            autoOpen: false,
            resizable: false,
            height:240,
            modal: true,
            dialogClass: "dialogWithDropShadow",
            buttons: {
                "Delete": function() {
                    var roleRtId = $("#roleDelRtId").html();
                    var roleUserId = $("#roleDelUserId").html();
                    var roleGroupId = $("#roleDelGroupId").html();

                    var args="rtId="+roleRtId+"&uId="+roleUserId+"&gId="+roleGroupId;

                    var roleOffset = $.cookie(offsetCookieRole);

                    $.get(urlRole+"?a=2&"+args, function(data) {
                        if (data == "1"){
                            showMessage("note-success", "Role delete succesfull.");
                            loadByAjax(paneRoleAjax, urlRole+"?uO="+roleOffset);
                        } else {
                            showMessage("note-error", "Unable to delete role.<br/>Error message: "+data);
                        }
                    });
                    $( this ).dialog( "close" );
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            }
        });
    });
</script>

<div id="dialog-role-delete" title="Delete user">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to delete role 
        <span id="roleDelUserId"></span> is 
        <span id="roleDelRtId"></span> in         
        <span id="roleDelGroupId"></span>
        ?</p>
</div>

<ul class="role iconlist idm-list">
    <c:forEach var="role" items="${roleBeanCol}">

        <li class="user">
            <form style="display:inline;">
                <input name="uId" type="hidden" value="${role.userId}"/>
                <input name="rtId" type="hidden" value="${role.roletype}"/>
                <input name="gId" type="hidden" value="${role.groupId}"/>
            </form>
            <a class="idm-lnk" href="#/"><span class="idm-name idm-name-margin idm-user-lnk">${role.userId}</span></a>
            is
            <a class="idm-lnk" href="#/"><span class="idm-name idm-rt-lnk">${role.roletype}</span></a>
            in 
            <a class="idm-lnk" href="#/"><span class="idm-name idm-group-lnk">${role.groupId}</span>(<span class="idm-name idm-groupType-lnk">${role.groupType}</span>)</a>

            <button class="idm-button idm-role-delete">Delete...</button>
        </li>

    </c:forEach>
</ul>