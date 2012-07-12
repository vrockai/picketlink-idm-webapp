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
        
        var rtOffset = $.cookie(offsetCookieRt);
        $("button.idm-role-rt-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var name = $(this).val();
            $("#roleRtId option").remove();
            $("#roleRtId").append($("<option></option>").attr("value",name).text(name));
            $(paneTabs).tabs('select', 2);
        });;
        
        $("button.idm-rt-roles").button({ icons: { primary: "ui-icon-tag" }}).click(function() {
            var rt = $(this).val();
            var url = urlRole+"?rt="+rt;
            loadByAjax(paneRoleAjax, url);
            $(paneTabs).tabs('select', 2);
        });
        
        $("button.idm-roletype-delete").button({ icons: { primary: "ui-icon-closethick" }}).click(function() {            
            $("#rtId").html($(this).attr("value"));
            $( "#dialog-rt-delete" ).dialog("open");
        });
        
        $( "#dialog-rt-delete" ).dialog({
            autoOpen: false,
            resizable: false,
            height:240,
            modal: true,
            dialogClass: "dialogWithDropShadow",
            buttons: {
                "Delete": function() {                    
                    $.get(urlRoletype+"?a=2&rtId="+$("#rtId").html(), function(data) {
                        if (data == "1"){
                            showMessage("note-success", "Roletype delete succesfull.");
                            createPaginator(urlRoletype, paneRoletypeAjax, handleRoletypePaginationClick);
                            loadByAjax(paneRoletypeAjax, urlRoletype+"?uO="+rtOffset);                            
                        } else {
                            showMessage("note-error", "Unable to delete roletype.<br/>Error message: "+data);    
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

<div id="dialog-rt-delete" title="Delete user">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to delete roletype <span id="rtId"></span>?</p>
</div>

<ul class="roletype iconlist idm-list">
    <c:forEach var="roletype" items="${roletypeList}">
        <li>
            <span class="idm-name idm-name-margin">${roletype.name}</span>
            <button value="${roletype.name}" class="idm-rt-roles">Show roles...</button>
            <button class="idm-button idm-roletype-delete" value="${roletype.name}">Delete...</button>
            <button class="idm-role-rt-add" value="${roletype.name}">Add to membership</button>
        </li>
    </c:forEach>
</ul>