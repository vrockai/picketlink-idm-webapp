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
    function handleRoletypePaginationClick(new_page_id, pagination_container) {
        return abstractPaginationHandler(new_page_id, pagination_container, urlRoletype, offsetCookieRt, paneRoletypeAjax);
    }
        
    $(function() {
        
        var rtOffset = initCookie(offsetCookieRt,0);
        setCookieFilter(filterCookieRt, "", filterElementRt);
        
        $("#idm-rt-reset").click(function() {            
            loadByAjax(paneRtAjax, urlRt);
        });
        
        $("button.idm-delete").button({ icons: { primary: "ui-icon-closethick" }});
        
        $("button.idm-roletype-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var name = $("#roletypeId").val();
            var args = "rtId="+name;
            $.get(urlRoletype+"?a=1&"+args, function(data) {
                if (data == "1"){
                    showMessage("note-success", "Roletype add succesfull.");
                    createPaginator(urlRoletype, paneRoletypeAjax, handleRoletypePaginationClick);
                    loadByAjax(paneRoletypeAjax, urlRoletype+"?uO="+rtOffset);                    
                } else {
                    showMessage("note-error", "Unable to add roletype.</br>Error message: "+data);    
                }
            });    
        });
        
        $("button.idm-children-open").button({ icons: { primary: "ui-icon-folder-collapsed" }}).click(function() {              
            $("#grp"+$(this).attr("value")).dialog("open");         
        });       
        
        $("#roletypeId").validate({
            messageContainerInvalid: $("#roletypeId").next(),
            validateFunction:function(){return true;}
        });  
        
        createPaginator(urlRoletype, paneRoletypeAjax, handleRoletypePaginationClick);       
    });
</script>

<form class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">
    <label for="roletypeId">Roletype:
        <span class="small msg">Add roletype name</span>        
    </label><input type="text" name="roletypeId" value="default-roletype" id="roletypeId" class="ui-widget ui-state-default ui-corner-all idm" />   
    <div class="error">tu bude validation error</div>
    <button class="idm-roletype-add" type="button">Add new roletype</button>
</form>

<h2 class="idm-list-header">Roletype list</h2>
<div class="idm-filter-pane">(filter: 
    <input disabled class="ui-widget ui-state-disabled ui-corner-all idm" value=""/>
    , to see all roletypes click <a href="#/" id="idm-rt-reset">here</a>)
</div>

<div id="MyRoletypeContentArea">
    <div class='p1 pagi'></div>
    <div class='conajax'><img src='${pageContext.request.contextPath}/img/ajax-loader.gif' class="idm-ajax-load"/></div>
    <div class='con'></div>
    <div class='p2 pagi'></div>
</div>
