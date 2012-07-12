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
        var uname = "${userAttBean.userId}";
        
        function handleAttPaginationClick(new_page_id, pagination_container) {            
            var attUrl = urlAtt+"?u="+uname;//+"&uO="+new_page_id;            
            //attOffset = new_page_id;
            //$.cookie(offsetCookieAtt, attOffset);
            //loadByAjax(paneAttAjax, url);
            return abstractPaginationHandler(new_page_id, pagination_container, attUrl, offsetCookieAtt, paneAttAjax);
            return false;
        }  
        
        var attOffset = $.cookie(offsetCookieAtt);
       
        $("button.idm-att-save").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var aname = $(this).siblings('input[name="idm-att-name-orig"]').val();
            var avalue = $(this).siblings('input[name="idm-att-value-orig"]').val();
            
            var anameNew = $(this).siblings('input[name="idm-att-name"]').val();
            var avalueNew = $(this).siblings('input[name="idm-att-value"]').val();
            
            editUrl = urlAtt+"?a=3&u="+uname+"&aN="+aname+"&aV="+avalue+"&aNn="+anameNew+"&aVn="+avalueNew;
            
            $.get(editUrl, function(data) {
                if (data == "1"){
                    showMessage("note-success", "Attribute edit succesfull.");
                    createPaginator(urlAtt+"?u="+uname, paneAttAjax, handleAttPaginationClick);
                } else {
                    showMessage("note-error", "Unable to edit attribute.<br/>Error message: "+data);    
                }
            });
        });
               
        $("button.idm-att-delete").button({ icons: { primary: "ui-icon-closethick" }}).click(function() {            
            var aname = $(this).val();
            $("#idm-att-del-id").html(aname);
            $("#dialog-att-delete").dialog("open");
        });
        
        $( "#dialog-att-delete" ).dialog({
            autoOpen: false,
            resizable: false,
            height:240,
            modal: true,
            dialogClass: "dialogWithDropShadow",
            buttons: {
                "Delete": function() {
                    var aname = $("#idm-att-del-id").html();
                    var delUrl = urlAtt+"?a=2&u="+uname+"&aN="+aname;
                    
                    $.get(delUrl, function(data) {
                        if (data == "1"){
                            showMessage("note-success", "Attribute delete succesfull.");
                            
                            createPaginator(urlAtt+"?u="+uname, paneAttAjax, handleAttPaginationClick);                            
                            //loadByAjax(paneAttAjax, urlAtt+"&u="+uname+"?uO="+attOffset);                            
                        } else {
                            showMessage("note-error", "Unable to delete attribute.<br/>Error message: "+data);    
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

<div id="dialog-att-delete" title="Delete user">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to delete attribute <span id="idm-att-del-id"></span>?</p>
</div>

<ul class="attribute iconlist idm-list">
    <c:forEach var="attribute" items="${userAttBean.attributes}">
        <li>
            <input type="hidden" name="idm-att-name-orig" value="${attribute.name}" class="ui-widget ui-state-default ui-corner-all idm"/>
            <input type="hidden" name="idm-att-value-orig" value="${attribute.value}" class="ui-widget ui-state-default ui-corner-all idm"/>
            <label>name: </label>
            <input type="text" name="idm-att-name" value="${attribute.name}" class="ui-widget ui-state-default ui-corner-all idm"/>

            <label>value: </label>
            <input type="text" name="idm-att-value" value="${attribute.value}" class="ui-widget ui-state-default ui-corner-all idm"/>

            <button value="${roletype.name}" class="idm-att-save">Save</button>
            <button class="idm-att-delete" value="${attribute.name}">Delete...</button>
        </li>
    </c:forEach>
</ul>