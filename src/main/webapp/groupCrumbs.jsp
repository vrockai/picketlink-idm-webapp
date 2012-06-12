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
        $("#breadcrumbs-one ul").children("li:last").addClass("current");
        
        $("#breadcrumbs-one a.idm-group").click(function(){
            
            var name = $(this).children("span.idm-group-name").html();
            var type = $(this).children("span.idm-group-type").html();
            
            var url = urlGroup+"?p="+name+"&t="+type;         
            $.get(urlGroup+"?a=6&p="+name+"&t="+type, function(data) {
                $("#groupCrumbs").html(data);
            });
            
            $.get("/jboss-idm-servlet/group?a=5&p="+name+"&t="+type, function(data) {
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
        
        $("a.idm-root-group").click(function(){  
            
            $.get(urlGroup+"?a=6", function(data) {
                $("#groupCrumbs").html(data);
            });
            
            loadByAjax(paneGroupAjax, urlGroup);
        });
        
        $("#breadcrumbs-one").children("li:last").children("a").addClass("current");
    });
</script>

<ul id="breadcrumbs-one">
    <li><a href="#/" class="idm-root-group">Root</a></li>
    <c:forEach var="parent" items="${parentList}">
        <li>
            <a class="idm-group" href="#/">
                <span class="idm-group-name">${parent.name}</span>
                (<span class="idm-group-type">${parent.type}</span>)
            </a>
        </li>
    </c:forEach>
</ul>