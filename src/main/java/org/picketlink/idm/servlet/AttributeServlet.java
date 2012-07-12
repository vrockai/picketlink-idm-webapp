/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.picketlink.idm.servlet;

import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.picketlink.idm.servlet.bean.UserBean;
import org.picketlink.idm.servlet.processor.IdmProcessor;

/**
 *
 * @author vrockai
 */
public class AttributeServlet extends IdmCrudServlet {

    private static Logger log = Logger.getLogger(AttributeServlet.class.getName());
    private static final long serialVersionUID = 331231441234123441L;

    @Override
    public void init() throws ServletException {
        this.jsp_page = "attributeList.jsp";
    }

    @Override
    protected void doIdmAdd(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String aName = getStringParameter(request, "aN", "");
        String aValue = getStringParameter(request, "aV", "");
        String userId = getStringParameter(request, "u", "");
        
        log.trace("Creating att for "+userId+" ("+aName+", "+aValue+")");
        
        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html");            
            idmProc.createAttribute(userId, aName, aValue);
            out.print(1);
        } catch (Exception e) {
            log.error(e);
            out.print(0);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmDelete(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String aName = getStringParameter(request, "aN", "");
        String userId = getStringParameter(request, "u", "");
    
        log.trace("Deleting att for "+userId+" ("+aName+")");
        
        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html");
            idmProc.deleteAttribute(userId, aName);            
            out.print(1);
        } catch (Exception e) {
            log.error(e);
            out.print(0);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmEdit(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String aName = getStringParameter(request, "aN", "");
        String aValue = getStringParameter(request, "aV", "");
        String aNameNew = getStringParameter(request, "aNn", "");
        String aValueNew = getStringParameter(request, "aVn", "");
        String userId = getStringParameter(request, "u", "");
    
        log.trace("Editing att for "+userId+" ("+aName+", "+aValue+") ->  ("+aNameNew+", "+aValueNew+")");
        
        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html");
            idmProc.editAttribute(userId, aName, aValue, aNameNew, aValueNew);            
            out.print(1);
        } catch (Exception e) {
            log.error(e);
            out.print(0);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmBrowse(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        int uPerPage = getIntParameter(request, "uPerPage", PERPAGE);
        int uOffset = getIntParameter(request, "uO", OFFSET) * uPerPage;
        String uQuery = getStringParameter(request, "q", QUERY);
        String userId = getStringParameter(request, "u", "");

        log.trace("Browsing atts: " + userId);
        
        UserBean userBean = idmProc.getAttributesByRange(uOffset, uPerPage, uQuery, userId);

        request.getSession().setAttribute("userAttBean", userBean);
        response.setContentType("text/html");
        request.getRequestDispatcher(jsp_page).forward(request, response);
    }

    @Override
    protected void doIdmCount(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String userId = getStringParameter(request, "u", "");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            int attNumber = 0;
            attNumber = idmProc.getAttributeCount(userId);
            log.info("Att count found: "+attNumber);
            out.print(attNumber);
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }
}
