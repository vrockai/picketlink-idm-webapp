<script>
    $(document).ready(function() {                
        $("#idm-search-button").button({ icons: { primary: "ui-icon-search" }}).click(function() {
            var query = $("#idm-search > form > input").val();
            var type = $("#idm-search-form").children("select").val();
            
            if (type == "u") {
                var url = "/jboss-idm-servlet/user?q="+query;
                loadByAjax(paneUserAjax, url);
                $(paneTabs).tabs('select', 0);
            } else if (type == "g") {
                var url = "/jboss-idm-servlet/group?q="+query;
                loadByAjax(paneGroupAjax, url);
                $(paneTabs).tabs('select', 1);
            } else if (type == "r") {
                var url = "/jboss-idm-servlet/roletype?q="+query;
                loadByAjax(paneRoletypeAjax, url);
                $(paneTabs).tabs('select', 3);
            } else if (type == "ugr") {
                var urlu = "/jboss-idm-servlet/user?q="+query;
                var urlg = "/jboss-idm-servlet/group?q="+query;
                var urlr = "/jboss-idm-servlet/roletype?q="+query;
                loadByAjax(paneUserAjax, urlu);
                loadByAjax(paneGroupAjax, urlg);
                loadByAjax(paneRoletypeAjax, urlr);
                $(paneTabs).tabs('select', 0);
            }            
        });
        
        function log( message ) {
            alert(message);
        }
          
        function ajaxListLoad(offsetValue,query,ext){            
            $("button.idm-pagi-but").removeClass("ui-state-focus");
            $("button.idm-pagi-but[value="+offsetValue+"]").addClass("ui-state-focus");
            
            $('#userAjaxLoader').show();
            $('#userPane').hide();
            
            $('#userPane').load('/jboss-idm-servlet/'+ext+'?uO='+offsetValue+'&q='+query, function(){
                $('#userAjaxLoader').hide();
                $('#userPane').fadeIn("slow");
            });
        }

        function setInputAutocomplete(type){            
            $( "#idm-search input" ).autocomplete({
            source: "/jboss-idm-servlet/auto?type="+type,
            minLength: 1
        });
        }
        
        setInputAutocomplete("u");        

        $("#idm-search-form").change(function(){
            var type = $("#idm-search-form").children("select").val();
            setInputAutocomplete(type);
        });
        
        function ajaxPaginatorLoad(offsetValue,element,ext){            
            $(element).hide();
            $(ajax).show();            
            $(element).load('/jboss-idm-servlet/'+ext+'?mode=true&uO='+offsetValue, function(){
                $(element).show();
                $(ajax).hide();
            });
        };
        
        function ajaxLoadList(offset,query,paginator,ext){            
            ajaxListLoad(offset,query,ext);
            ajaxPaginatorLoad(offset, paginator,ext);
        }
    });
</script>
<div id="idm-search">
    <form class="idm-form" id="idm-search-form">
        <!--label>Search: </label-->
        <input value="" class="ui-widget ui-state-default ui-corner-all idm"/>

        <select class="ui-widget ui-state-default ui-corner-all idm" name="mode">
            <option value="ugr">Everything</option>
            <option value="u" selected>User</option>
            <option value="g">Group</option>
            <option value="r">Roletype</option>
        </select>

        <button id="idm-search-button" type="button">Search</Button>
    </form>
</div>