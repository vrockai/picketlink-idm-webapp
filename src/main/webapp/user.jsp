<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script>

    function handleUserPaginationClick(new_page_id, pagination_container) {
        return abstractPaginationHandler(new_page_id, pagination_container, urlUser, offsetCookieUser, paneUserAjax);
    }

    $(document).ready(function() {

        setCookieFilter(filterCookieUser, "", filterElementUser);
        $("#idm-lnk-user-reset").click(function() {
            loadByAjax(paneUserAjax, urlUser);
        });

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
                    var uPw2 = $("#idm-user-create-passconf").val();
                    var args = "uId="+uId+"&uFn="+uFn+"&uLn="+uLn+"&uEm="+uEm+"&uPw="+uPw+"&uPw2="+uPw2;
                    var createUserUrl = urlUser+"?a=1&"+args;
                    success = ajaxActionWithRefresh(createUserUrl, "User add succesfull.", "Unable to add user.", urlUser, paneUserAjax, handleUserPaginationClick);
                    if (success) {
                        $(this).dialog( "close" );
                    }
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
    <div class="error">non-valid e-mail address</div>
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
        <input type="password" name="password" value="" id="idm-user-create-pass" class="ui-widget ui-state-default ui-corner-all idm" />
        <label class="ui-widget" for="password">Password confirmation:
            <span class="small">Confirm your password</span>
        </label>
        <input type="password" name="password" value="" id="idm-user-create-passconf" class="ui-widget ui-state-default ui-corner-all idm" />
    </form>
</div>

<h2 class="idm-list-header">User list</h2>
<div class="idm-filter-pane">(filter:
    <input disabled class="ui-widget ui-state-disabled ui-corner-all idm" id="idm-user-reset" value=""/>
    , to see all users click <a href="#/" id="idm-lnk-user-reset">here</a>)
</div>

<div id="MyContentArea"></div>