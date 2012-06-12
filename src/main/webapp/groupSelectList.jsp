<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
    $(function() {
        $("button.idm-group-select").button({ icons: { primary: "ui-icon-closethick" }}).click(function(){
            var name = $(this).siblings('input[name="gId"]').val();
            var type = $(this).siblings('input[name="gType"]').val();
            handlerGroupSel(name, type);
            
            $("#dialog-group-select").dialog("close");
        });
    });
</script>

<ul class="group iconlist idm-list">
    <c:forEach var="group" items="${groupList}">
        <li class="<c:choose><c:when test="${group.parent}">idm-parent-group</c:when><c:otherwise>idm-terminal-group</c:otherwise></c:choose>">
            <input type="hidden" name="gId" value="${group.name}"/>
            <input type="hidden" name="gType" value="${group.type}"/>
                    <span class="idm-name idm-name-margin">${group.name}</span>
                    <div class="idm-col">
                        <span class="idm-type">${group.type}</span>
                        <span class="idm-group-children">(children: ${group.childrenCount})</span>
                    </div>
            <button value="${group.name}" class="idm-group-select">Select</button>
        </li>
    </c:forEach>
</ul>