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

        var userOffset = $.cookie(offsetCookieUser);

        $("button.idm-edit-open").button({ icons: { primary: "ui-icon-pencil" }});
       
        $("button.idm-user-roles").button({ icons: { primary: "ui-icon-tag" }}).click(function() {
            var user = $(this).val();
            var url = urlRole+"?u="+user;
            loadByAjax(paneRoleAjax, url);
            $(paneTabs).tabs('select', 2);
        });

        $("button.idm-role-user-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var name = $(this).val();
            $("#roleUserId option").remove();
            $("#roleUserId").append($("<option></option>").attr("value",name).text(name));
            $(paneTabs).tabs('select', 2);
        });

        $("button.idm-user-assign").button({ icons: { primary: "ui-icon-plus" }}).click(function(){
            var uid = $(this).val();
            handlerGroupSel = function(name, type) {
                var assUrl = urlUser+"?a=8&uId="+uid+"&gId="+name+"&gT="+type;
                ajaxActionWithRefresh(assUrl, "Group association succesfull!", "Unable to associate user.", urlUser, paneUserAjax, handleUserPaginationClick);
                $("#dialog-group-select").dialog("close");
            };
            $("#dialog-group-select").dialog("open");
        });

        $("button.idm-atts-open").button({ icons: { primary: "ui-icon-folder-collapsed" }});
        $("button.idm-atts-add").button({ icons: { primary: "ui-icon-plus" }});
        $("button.idm-delete").button({ icons: { primary: "ui-icon-closethick" }});
        $("div.idm-but-upload").button({ icons: { primary: "ui-icon-folder-open"}});
        $("button.idm-but-change").button({ icons: { primary: "ui-icon-key" }});

        $("button.idm-user-group").button({ icons: { secondary: "ui-icon-closethick" }}).click(function() {
            var uid = $(this).siblings('input[name="uid"]').val();
            var gname = $(this).val();
            var gtype = $(this).siblings('input[name="gtype"]').val();

            $("#gUserId").html(uid);
            $("#gGroupId").html(gname);
            $("#gGroupType").html(gtype);

            $( "#dialog-user-deassociate" ).dialog("open");
        });

        $("button.idm-user-photo-save").button({ icons: { primary: "ui-icon-pencil"}});
        
        $("button.idm-user-edit-save").unbind();
        $("button.idm-user-edit-save").button({ icons: { primary: "ui-icon-pencil"}}).click(function() {
            var uname = $(this).siblings('input[name="uname"]').val();
            var fname = $(this).siblings('input[name="fname"]').val();
            var lname = $(this).siblings('input[name="lname"]').val();
            var email = $(this).siblings('input[name="email"]').val();

            editUrl = urlUser+"?a=3&uId="+uname+"&uFn="+fname+"&uLn="+lname+"&uEm="+email;

            ajaxAction(editUrl, "Attribute edit succesfull.", "Unable to edit attribute." );     
        });

        $("button.idm-user-edit-passwd").unbind();
        $("button.idm-user-edit-passwd").button({ icons: { primary: "ui-icon-pencil"}}).click(function() {
            var uname = $(this).siblings('input[name="uname"]').val();
            var pass1 = $(this).siblings('input[name="newPass"]').val();
            var pass2 = $(this).siblings('input[name="newPass2"]').val();

            editUrl = urlUser+"?a=7&uId="+uname+"&uP1="+pass1+"&uP2="+pass2;

            ajaxAction(editUrl, "Password change succesfull.", "Unable to change password." );
        });
 
        $("button.idm-delete").click(function() {
            $("#userId").html($(this).attr("value"));
            $( "#dialog-user-delete" ).dialog("open");
        });

        $( "#dialog-user-delete" ).dialog({
            autoOpen: false,
            resizable: false,
            height:240,
            modal: true,
            dialogClass: "dialogWithDropShadow",
            buttons: {
                "Delete": function() {
                    var actionUrl = urlUser+"?a=2&uId="+$("#userId").html();
                    ajaxActionWithRefresh(actionUrl, "User delete succesfull!", "Unable to delete user", urlUser, paneUserAjax, handleUserPaginationClick);
                    $( this ).dialog( "close" );
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            }
        });

        $( "#dialog-user-deassociate" ).dialog({
            autoOpen: false,
            resizable: false,
            height: 240,
            modal: true,
            dialogClass: "dialogWithDropShadow",
            buttons: {
                "Delete": function() {
                    var uid = $("#gUserId").html();
                    var gname = $("#gGroupId").html();
                    var gtype = $("#gGroupType").html();
                    var deAssUrl = urlUser+"?a=9&uId="+uid+"&gId="+gname+"&gT="+gtype;
                    ajaxActionWithRefresh(deAssUrl, "Group deassociation succesfull!", "Unable to deassociate user", urlUser, paneUserAjax, handleUserPaginationClick);
                    $( this ).dialog( "close" );
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            }
        });

        $("button.idm-edit-open").click(function() {
            $("#edit"+$(this).attr("value")).dialog("open");
        });
    });
</script>
<div id="dialog-user-delete" title="Delete user">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to delete user <span id="userId"></span>?</p>
</div>

<div id="dialog-user-deassociate" title="Deassocite user">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to deassociate user <span id="gUserId"></span> from group <span id="gGroupId"></span>(<span id="gGroupType"></span>)?</p>
</div>

<script>
    $(document).ready(function() {

        var attOffset = $.cookie(offsetCookieAtt);
        
        $("button.idm-atts-open").click(function() {            
            $( "#dialog-user-atts" ).dialog("open");
            var userId = $(this).val();
            $("#idm-user-att").html(userId);

            function handleAttPaginationClick(new_page_id, pagination_container) {
                return abstractPaginationHandler(new_page_id, pagination_container, urlAtt+"?u="+userId, offsetCookieAtt, paneAttAjax);
            }
            createPaginator(urlAtt+"?u="+userId, paneAttAjax, handleAttPaginationClick);

            $("#idm-att-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
                var aname = $("#idm-att-add-name").val();
                var avalue = $("#idm-att-add-value").val();
                var userId = $("#idm-user-att").html();
                args = "u="+userId+"&aN="+aname+"&aV="+avalue;
                var attAddUrl = urlAtt+"?a=1&"+args;
                ajaxActionWithRefresh(attAddUrl, "Attribute add succesfull.", "Unable to add attribute.", urlAtt+"?u="+userId, paneAttAjax, handleAttPaginationClick);
            });
        });

        var att_roles_button = $("button.idm-att-roles").button({ icons: { primary: "ui-icon-tag" }})
  
        att_roles_button.click(function() {
            var user = $(this).val();
            var url = urlAtt+"?u="+user;
            loadByAjax(paneAttAjax, url);
            $(paneTabs).tabs('select', 2);
        });
  
        $('input[name="photo"]').change(function(e){

            var userHash = $(this).parent().parent().siblings('input[name="hash"]').val();
            
            file = $(this).prop("files")[0];

            var reader = new FileReader();

            reader.onload = function (e) {
                $('#imgEdit'+userHash)
                .attr('src', e.target.result);
            };

            reader.readAsDataURL(file);
        });

        $("button.idm-user-photo-save").unbind();
        $("button.idm-user-photo-save").click(function(){            
            var formData = new FormData($(this).parent().parent('form')[0]);
            var userHash = $(this).siblings('input[name="hash"]').val();
            var userId = $(this).siblings('input[name="id"]').val();

            $("#imgList"+userHash).attr("src","img/282.gif");
            $.ajax({
                url: urlPhoto,  //server script to process data
                type: 'POST',
                beforeSend: function(){;},
                success: function(){
                    var newPhotoUrl = urlPhoto+"?u="+userId;
                    showMessage("note-success", "User photo was uploaded."+newPhotoUrl);
                    d = new Date();
                    $("#imgList"+userHash).attr("src",newPhotoUrl+"&d="+d);
                    $("#imgEdit"+userHash).attr("src",newPhotoUrl+"&d="+d);
                },
                complete: function(){
                    
                },
                error: function(){
                    showMessage("note-error", "Unable to upload user photo.");                    
                },
                // Form data
                data: formData,
                //Options to tell JQuery not to process data or worry about content-type
                cache: false,
                contentType: false,
                processData: false
            });            
        });

        $('a.idm-lightbox-lnk').lightBox({fixedNavigation:true});
        
        $( "#dialog-user-atts" ).dialog({
            autoOpen: false,
            modal: true,
            width: 900,
            dialogClass: "dialogWithDropShadow",
            title: "Attribute editor"
        });

    });

</script>

<div id="dialog-user-atts">
    <form class="idm-inline-form ui-widget ui-widget-content ui-corner-all ui-idm-header">
        <label for="idm-att-add-name">Attribute name:
            <span class="small msg">Add attribute name:</span>
        </label>

        <input type="text" name="AttName" value="*" id="idm-att-add-name" class="ui-widget ui-state-default ui-corner-all idm" />

        <label for="idm-att-add-value">Attribute value:
            <span class="small msg">Add attribute value:</span>
        </label>

        <input type="text" name="AttType" value="*" id="idm-att-add-value" class="ui-widget ui-state-default ui-corner-all idm" />

        <button id="idm-att-add" type="button">Add attribute</button>
    </form>

    <h2>Attribute list of user <span id="idm-user-att"></span></h2>

    <div id="MyAttContentArea"></div>

</div>

<ul class="user idm-list">
    <c:forEach var="user" items="${userPagiList}">
        <li class="user">

            <a class="idm-lightbox-lnk idm-user-photo" href="${pageContext.request.contextPath}/photo?u=${user.userId}&x=<%= System.nanoTime() %>"><img src='${pageContext.request.contextPath}/photo?u=${user.userId}&x=<%= System.nanoTime() %>' id="imgList${user.hash}" width="90" class='idm-user-photo'/></a>

            <div class="idm-name idm-name-margin">${user.userId}</div>

            <div class="idm-toolbar">
                <span>actions: </span>
                <button value="${user.userId}" class="idm-user-roles">Show roles...</button>
                <button value="${user.hash}" class="idm-edit-open">Edit...</button>
                <button value="${user.hash}" value="${user.userId}" class="idm-atts-open">Attributes...</button>
                <button class="idm-delete" value="${user.userId}">Delete...</button>
                <button class="idm-role-user-add" value="${user.userId}">Add to membership</button>
            </div>

            <div class="idm-toolbar">
                <span>groups: </span>
                <ul class="ui-widget ui-widget-content ui-corner-all idm-ul-inline">
                    <c:forEach var="userGroup" items="${user.associatedGroups}">
                        <li>
                            <input type="hidden" name="uid" value="${user.userId}">
                            <input type="hidden" name="gtype" value="${userGroup.groupType}">
                            <button class="idm-user-group" value="${userGroup.name}">${userGroup.name}</button>
                        </li>
                    </c:forEach>
                </ul>

                <button class="idm-user-assign" value="${user.userId}">Assign...</button>
            </div>

            <script>
                $(document).ready(function() {
                    //alert(${user.hash});
                    $( "#edit${user.hash}").dialog({
                        autoOpen: false,
                        modal: false,
                        width: 450,
                        dialogClass: "dialogWithDropShadow",
                        title: "${user.userId} edit"
                    });
                    
                    $("#edit${user.hash} button").button();
                });
            </script>

            <div id="edit${user.hash}"class="dialog-edit">

                <form class="idm-basic-form" enctype="multipart/form-data" action="http://localhost:8080/jboss-idm-servlet/photo" method="post">
                    <fieldset>
                        <legend>User photo</legend>
                        <label>User photo:
                            <span class="small">Add your user ID</span>
                        </label><img src='${pageContext.request.contextPath}/photo?u=${user.userId}' width="90" id="imgEdit${user.hash}" class='idm-user-photo' style="margin-right:100px"/>
                        <input  type="hidden" name="id" value="${user.userId}"/>
                        <label>Upload file:
                            <span class="small">Choose your new photo:</span>
                        </label>
                        <div class="idm-but-upload idm idm-button" style="margin-left:0px;">Upload photo<input type="file" name="photo" style="opacity: 0; filter: alpha(opacity=0); position: absolute; right: 0px; top: 0; font-size: 40px;"/></div>
                        <input  type="hidden" name="hash" value="${user.hash}"/>
                        <!--<input type="submit" value="Add Photo"/>-->
                        <button class="idm-user-photo-save" type="button">Save</button>
                    </fieldset>
                </form>

                <form class="idm-basic-form">
                    <fieldset>
                        <input type="hidden" name="uname" value="${user.userId}"/>
                        <legend>Basic attributes</legend>
                        <label>First name:
                            <span class="small">Add user 1st name</span>
                        </label><input type="text" name="fname" value="${user.fname}" class="ui-widget ui-state-default ui-corner-all idm"/>
                        <label>Last name:
                            <span class="small">Add user last name</span>
                        </label><input type="text" name="lname" value="${user.lname}" class="ui-widget ui-state-default ui-corner-all idm"/>
                        <label>E-mail:
                            <span class="small">Add user e-mail</span>
                        </label><input type="text" name="email" value="${user.email}" class="ui-widget ui-state-default ui-corner-all idm"/>
                        <button class="idm-user-edit-save" type="button">Save</button>
                    </fieldset>
                </form>

                <form class="idm-basic-form">
                    <fieldset>
                        <input type="hidden" name="uname" value="${user.userId}"/>
                        <legend>Password change</legend>
                        <label>New password:
                            <span class="small">Add new password</span>
                        </label><input type="password" name="newPass" class="ui-widget ui-state-default ui-corner-all idm"/>
                        <label>Password confirmation:
                            <span class="small">Confirm new password</span>
                        </label><input type="password" name="newPass2" class="ui-widget ui-state-default ui-corner-all idm" />
                        <button class="idm-user-edit-passwd" type="button">Change password</button>
                    </fieldset>
                </form>
            </div>
        </li>
    </c:forEach>
</ul>