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
        <LINK REL=StyleSheet HREF="${pageContext.request.contextPath}/css/jquery.lightbox-0.5.css" TYPE="text/css" MEDIA=screen>
        <link rel="icon" href="${pageContext.request.contextPath}/img/picketlink_icon_16x.png" type="image/PNG">
        <script src="${pageContext.request.contextPath}/js/jquery-1.7.1.min.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/jquery.cookie.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/jquery-ui-1.8.18.custom.min.js" type="text/javascript"></script>
        <script src="${pageContext.request.contextPath}/js/idm.js" type="text/javascript"></script>
        <script>
            $(document).ready(function() {
                $("button.idm-populate").button({ icons: { primary: "ui-icon-check" }}).click(function(){
                    var actionUrl = urlIdm+"?populate=1";                    
                    $.get(actionUrl, function(data) {
                        if (data == "1"){
                            location.reload();
                        } else {
                            showMessage("note-error", "Unable to prepopulate database.<br/>Error message: "+data);
                        }
                    });
                });
                $("button.idm-continue").button({ icons: { primary: "ui-icon-seek-next" }}).click(function() {
                    location.reload();
                });
            });
        </script>
        <style type="text/css">

        </style>
    </head>
    <body class="idm-intro">
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
        
        <div class="main">            
            <h1>Picketlink IDM GUI</h1>
            <div class="ui-widget">
                <div class="ui-state-highlight ui-corner-all" style="margin-top: 20px; padding: 0 .7em;"> 
                    <p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                        The database configured with these application instance seems to
                        be empty. If this is your first start, we recommend to automatically 
                        populate the database with example data. Do you want to populate the
                        data or continue to use the web GUI with empty database? If you
                        choose to continue, you can still populate the data anytime you want
                        in the preferences tab.
                    </p>
                </div>
            </div>
            <br/>
            <button class="idm-populate" type="button">Populate database</button>
            <button class="idm-continue" type="button">Continue</button>
        </div>
    </body>
</html>