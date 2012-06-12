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
    
    $(document).ready(function() {
        
        var userOffset = initCookie(offsetCookieUser, 0);
        
        setCookieFilter(filterCookieUser, "", filterElementUser);
        $("#idm-lnk-user-reset").click(function() {            
            loadByAjax(paneUserAjax, urlUser);
        });
        
        function handleUserPaginationClick(new_page_id, pagination_container) {
            var url = urlUser+"?uO="+new_page_id;            
            userOffset = new_page_id;
            $.cookie(offsetCookieUser, userOffset);
            loadByAjax(paneUserAjax, url);
            return false;
        }  
                
        // E-mail validation
        var ck_email = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
                
        var validate = function(text){
            return ck_email.test(text);
        }
        
        $("#emailInp").keyup(function(){
            var val = $(this).val();
            if (!validate(val)){
                $("#emailVal").html("Error")
            } else {
                $("#emailVal").html("OK")
            }
        });
    
        $("button").button();
        $("#idm-photo-up").button({ icons: { primary: "ui-icon-pencil" }});
        
        $("#idm-user-list-refresh").button({ icons: { primary: "ui-icon-refresh" }}).click(function() {            
            loadByAjax(paneUserAjax, urlUser);
            createPaginator(urlUser, paneUserAjax, handleUserPaginationClick);
        });
    
        $(".idm-user-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var email = $("#emailInp").val();
            $("#idm-user-create-id").val(email.split("@")[0]);
            $("#idm-user-create-email").val(email);
            $("#create-user-dialog-form" ).dialog( "open" );
        });          
    
        $( "#create-user-dialog-form" ).dialog({
            autoOpen: false,
            width: 450,
            modal: true,
            buttons: {
                "Create an account": function() {
                    var uId = $("#idm-user-create-id").val();
                    var uFn = $("#idm-user-create-fname").val();
                    var uLn = $("#idm-user-create-lname").val();
                    var uEm = $("#idm-user-create-email").val();
                    var uPw = $("#idm-user-create-pass").val();                    
                    var args = "uId="+uId+"&uFn="+uFn+"&uLn="+uLn+"&uEm="+uEm+"&uPw="+uPw;                    
                    $.get(urlUser+"?a=1&"+args, function(data) {
                        if (data == "1"){
                            showMessage("note-success", "User add succesfull.");
                            loadByAjax(paneUserAjax, urlUser+"?uO="+userOffset);
                        } else {
                            showMessage("note-error", "Unable to add user<br/>Error message: "+data);    
                        }
                    });
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
                        primary: 'ui-icon-person'
                    }
                }).next().button({
                    icons: {
                        primary: 'ui-icon-cancel'
                    }
                });
            }
        });
        
        createPaginator(urlUser, paneUserAjax, handleUserPaginationClick);
               
        function pickLabel(inputElement){
            var inputName = $(inputElement).attr("name");
            //alert(inputName);
            return $(inputElement).siblings("label[for="+inputName+"]");
        }
                        
        $("#emailInp").validate({
            messageContainerInvalid: $("#emailInp").next(),
            validateFunction:validate
        });       
    });
      
</script>

<form class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">
    <label for="userId">E-mail:
        <span class="small msg">Add user e-mail</span>        
    </label><input type="text" name="userId" value="foo@redhat.com" id="emailInp" class="ui-widget ui-state-default ui-corner-all idm" />   
    <div class="error">tu bude validation error</div>
    <button class="idm-user-add" type="button">Add new user...</button>
</form>

<div id="create-user-dialog-form" title="Create new user">    
    <form class="idm-basic-form">
        <label class="ui-widget" for="uId">User ID:
            <span class="small">Add your user ID</span>
        </label><input type="text" name="uId" id="idm-user-create-id" class="ui-widget ui-state-default ui-corner-all idm"/>                
        <label class="ui-widget">First name:
            <span class="small">Add your 1st name</span>
        </label><input type="text" id="idm-user-create-fname" class="ui-widget ui-state-default ui-corner-all idm"/>
        <label class="ui-widget">Last name:
            <span class="small">Add your last name</span>
        </label><input type="text" id="idm-user-create-lname" class="ui-widget ui-state-default ui-corner-all idm"/>
        <label class="ui-widget">E-mail:
            <span class="small">Add your e-mail</span>
        </label><input type="text" id="idm-user-create-email" class="ui-widget ui-state-default ui-corner-all idm"/>                    
        <label class="ui-widget" for="password">Password:
            <span class="small">Add your password</span>
        </label>
        <input type="password" name="password" id="password" value="" id="idm-user-create-pass" class="ui-widget ui-state-default ui-corner-all idm" />        
        <label class="ui-widget" for="password">Password confirmation:
            <span class="small">Confirm your password</span>
        </label>
        <input type="password" name="password" id="password" value="" id="idm-user-create-passconf" class="ui-widget ui-state-default ui-corner-all idm" />        
    </form>
</div>

<h2 class="idm-list-header">User list</h2>
<div class="idm-filter-pane">(filter: 
    <input disabled class="ui-widget ui-state-disabled ui-corner-all idm" id="idm-user-reset" value=""/>
    , to see all users click <a href="#/" id="idm-lnk-user-reset">here</a>)
</div>

<div id="MyContentArea">
    <div class='p1 pagi'></div>
    <div class='conajax'><img src='${pageContext.request.contextPath}/img/ajax-loader.gif' class="idm-ajax-load" /></div>
    <div class='con'></div>
    <div class='p2 pagi'></div>
</div>