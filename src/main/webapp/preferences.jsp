<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
    $(document).ready(function() {
        $("button.idm-but-apply").button({ icons: { primary: "ui-icon-check" }}).click(function(){
            var actionUrl = urlIdm+"?populate=1";                    
            $.get(actionUrl, function(data) {
                if (data == "1"){
                    location.reload();
                } else {
                    showMessage("note-error", "Unable to prepopulate database.<br/>Error message: "+data);
                }
            });
        });
    });
</script>

<h2>User preferences</h2>
<form class="idm-setup idm-basic-form">

    <fieldset class="idm-pref-formpane">        
        <legend>Theme properties</legend>
        <div id="switcher"></div>        
    </fieldset>

    <fieldset class="idm-pref-formpane"> 
        <legend>Application tools</legend>

        <label>Populate database:
            <span class="small">Name of the binary attribute used for user avatar</span>
        </label><button type="button" class="idm-but-apply" style="margin:0px auto 0px 20px;">Apply</button>
    </fieldset>

</form>
