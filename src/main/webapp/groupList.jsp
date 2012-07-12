<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
    $(function() {
        var groupOffset = $.cookie(offsetCookieGroup);
        
        $("button.idm-group-delete").button({ icons: { primary: "ui-icon-closethick" }});
        
        $("button.idm-assign").button({ icons: { primary: "ui-icon-plus" }}).click(function(){            
            var pId = $(this).siblings('input[name="gId"]').val();
            var pType = $(this).siblings('input[name="gType"]').val();
            
            handlerGroupSel = function(name, type) {
                var assUrl = urlGroup+"?a=8&pId="+name+"&pT="+type+"&gId="+pId+"&gT="+pType;
                ajaxActionWithRefresh(assUrl, "Group association succesfull!", "Unable to associate group.", urlGroup, paneGroupAjax, handleGroupPaginationClick);
                $("#dialog-group-select").dialog("close");
            };
            $("#dialog-group-select").dialog("open");
        });
                
        $("button.idm-grp-deassign").button({ icons: { primary: "ui-icon-minus" }}).click(function(){
            var pName= $(this).siblings('input[name="pName"]').val();
            var pType= $(this).siblings('input[name="pType"]').val();
            var gName= $(this).siblings('input[name="gName"]').val();
            var gType= $(this).siblings('input[name="gType"]').val();            
            var deassUrl = urlGroup+"?a=9&pId="+pName+"&pT="+pType+"&gId="+gName+"&gT="+gType;
            ajaxActionWithRefresh(deassUrl, "Group disassociation succesfull!", "Unable to disassociate group.", urlGroup, paneGroupAjax, handleGroupPaginationClick);
            $(this).parents(".dialog-children").dialog("close");
        });
        
        $("button.idm-role-group-add").button({ icons: { primary: "ui-icon-plus" }}).click(function() {
            var name = $(this).val();
            $("#roleGroupId option").remove();
            $("#roleGroupId").append($("<option></option>").attr("value",name).text(name));
            $(paneTabs).tabs('select', 2);
        });        
        
        $("button.idm-group-roles").button({ icons: { primary: "ui-icon-tag" }}).click(function() { 
            var name = $(this).siblings('input[name="gId"]').val();
            var type = $(this).siblings('input[name="gType"]').val();
            
            function handleGroupRolePaginationClick(new_page_id, pagination_container) {
                //var handlerUrl = urlRole+"?g="+name+"&t="+type+"&uO="+new_page_id;            
                var handlerUrl = urlRole+"?g="+name+"&t="+type;
                //var ajaxPane = paneRoleAjax;
                //roleOffset = new_page_id;
                //$.cookie(offsetCookieRole, roleOffset);
                //loadByAjax(ajaxPane, handlerUrl);            
                return abstractPaginationHandler(new_page_id, pagination_container, handlerUrl, offsetCookieRole, paneRoleAjax);
            }
            
            var url = urlRole+"?g="+name+"&t="+type;
            //loadByAjax(paneRoleAjax, url);
            createPaginator(url, paneRoleAjax, handleGroupRolePaginationClick);
            $(paneTabs).tabs('select', 2);
        });
        
        $("button.idm-children-open").button({ icons: { primary: "ui-icon-folder-collapsed" }}).click(function() {              
            $("#grp"+$(this).attr("value")).dialog("open");         
        });
        
        $(".group a.idm-group").click(function(){
            
            var name = $(this).children("span.idm-group-name").html();
            var type = $(this).siblings("div.idm-col").children("span.idm-type").children("span.idm-group-type").html();
                        
            $.cookie(groupParentId,name);
            $.cookie(groupTypeId,type);
                        
            var url = urlGroup+"?p="+name+"&t="+type;         
            
            $.get(urlGroup+"?a=6&p="+name+"&t="+type, function(data) {
                $(paneGroupBreadcrums).html(data);
            });            
            
            $.get(urlGroup+"?a=5&p="+name+"&t="+type, function(data) {
                var groupCount = data;                
                $(paneGroupAjax+" div.p1, "+paneGroupAjax+" div.p2").pagination({
                    maxentries: groupCount,
                    contentPane: paneGroupAjax,
                    items_per_page:5, 
                    callback:handleGroupPaginationClick
                });
            });
            
            loadByAjax(paneGroupAjax, url);        
        });
        
        $("button.idm-group-delete").click(function() {            
            var name = $(this).siblings('input[name="gId"]').val();
            var type = $(this).siblings('input[name="gType"]').val();
            
            $("#dialog-group-delete span.groupId").html(name);
            $("#dialog-group-delete span.groupType").html(type);
            
            $("#dialog-group-delete").dialog("open");
        });
        
        $( "#dialog-group-delete" ).dialog({
            autoOpen: false,
            resizable: false,
            height:240,
            modal: true,
            dialogClass: "dialogWithDropShadow",
            buttons: {
                "Delete": function() {                                        
                    var name = $("#dialog-group-delete span.groupId").html();
                    var type= $("#dialog-group-delete span.groupType").html();
                    var groupActionUrl = urlGroup+"?a=2&gId="+name+"&t="+type;
                    ajaxActionWithRefresh(groupActionUrl, "Group delete succesfull!", "Unable to delete group.", urlGroup, paneGroupAjax, handleGroupPaginationClick);                    
                    $( this ).dialog( "close" );
                },
                Cancel: function() {
                    $( this ).dialog( "close" );
                }
            }
        });        
    });
</script>
<div id="dialog-group-delete" title="Delete group">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Are you sure you want to delete group <span class="idm-bold groupId"></span> of type <span class="idm-bold groupType"></span>?</p>
</div>
<ul class="group iconlist idm-list">
    <c:forEach var="group" items="${groupList}">
        <li class="<c:choose><c:when test="${group.parent}">idm-parent-group</c:when><c:otherwise>idm-terminal-group</c:otherwise></c:choose>">
            <input type="hidden" name="gId" value="${group.name}"/>
            <input type="hidden" name="gType" value="${group.type}"/>

            <c:choose>
                <c:when test="${group.parent}">
                    <a href="#/" class="idm-lnk idm-group">
                        <span class="idm-name idm-group-name idm-name-margin">${group.name}</span>
                    </a>
                    <div class="idm-col">
                        <span class="idm-type"><span class="idm-group-type">${group.type}</span></span>
                        <span class="idm-group-children">(children: ${group.childrenCount})</span>
                    </div>

                </c:when>
                <c:otherwise>
                    <span class="idm-name idm-name-margin">${group.name}</span>
                    <div class="idm-col">
                        <span class="idm-type">${group.type}</span>
                        <span class="idm-group-children">(children: ${group.childrenCount})</span>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:choose>
                <c:when test="${group.parent}">
                    <script>
                        $(document).ready(function() {
                            $("#grp${group.hash}").dialog({
                                autoOpen: false,
                                width: 600,
                                dialogClass: "dialogWithDropShadow",                        
                                title: "${group.name} children"
                            });
                        });
                    </script>
                    <div id="grp${group.hash}" class="dialog-children">                
                        <ul class="group iconlist">                        
                            <c:forEach var="child" items="${group.children}">                        
                                <li class="<c:choose><c:when test="${child.parent}">idm-parent-group</c:when><c:otherwise>idm-terminal-group</c:otherwise></c:choose>">
                                    <input type="hidden" name="pName" value="${group.name}"/>
                                    <input type="hidden" name="pType" value="${group.type}"/>
                                    <input type="hidden" name="gName" value="${child.name}"/>
                                    <input type="hidden" name="gType" value="${child.type}"/>
                                    <c:choose>
                                        <c:when test="${child.parent}">
                                            <a href="#/" class="idm-lnk idm-name-margin idm-group">
                                                <span class="idm-name idm-group-name">${child.name}</span>
                                            </a>
                                            <div class="idm-col">
                                                <span class="idm-type"><span class="idm-group-type">${child.type}</span></span>
                                                <span class="idm-group-children">(children: ${child.childrenCount})</span>
                                            </div>                                 
                                        </c:when>
                                        <c:otherwise>
                                            <span class="idm-name idm-name-margin">${child.name}</span>
                                            <div class="idm-col">
                                                <span class="idm-type">${child.type}</span>
                                                <span class="idm-group-children">(children: ${child.childrenCount})</span>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <button class="idm-grp-deassign">Deassign...</button>
                                </li>
                            </c:forEach>                  
                        </ul>                
                    </div>
                    <button value="${group.hash}" class="idm-children-open">Show children...</button>
                </c:when>
            </c:choose>

            <button value="${group.name}" class="idm-group-roles">Show roles...</button>
            <button value="assign" class="idm-assign">Assign into...</button>
            <button value="delete" class="idm-group-delete">Delete...</button>
            <button class="idm-role-group-add" value="${group.name}">Add to membership</button>

        </li>
    </c:forEach>
</ul>