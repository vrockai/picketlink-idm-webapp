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
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.servlet.processor.IdmProcessor;

/**
 *
 * @author vrockai
 */
public class RoletypeServlet extends IdmCrudServlet {

    private static Logger log = Logger.getLogger(RoletypeServlet.class.getName());
    private static final long serialVersionUID = 33123141234423441L;

    @Override
    public void init() throws ServletException {
        this.jsp_page = "roletypeList.jsp";
    }

    @Override
    protected void doIdmAdd(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            String rtId = request.getParameter("rtId");
            log("Add roletype: " + rtId);
            idmProc.createRoletype(rtId);
            out.print("1");
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());

        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmDelete(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            String rtId = request.getParameter("rtId");
            idmProc.deleteRoletype(rtId);
            out.print("1");
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmEdit(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void doIdmBrowse(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        int uPerPage = getIntParameter(request, "uPerPage", PERPAGE);
        int uOffset = getIntParameter(request, "uO", OFFSET) * uPerPage;
        String uQuery = getStringParameter(request, "q", QUERY);
        Collection<RoleType> roletypeList = idmProc.getRoletypesByRange(uOffset, uPerPage, uQuery);// idmProc.getAllRoletypes();
        log.trace("rtC:" + roletypeList.size());
        request.getSession().setAttribute("roletypeList", roletypeList);
        response.setContentType("text/html");
        request.getRequestDispatcher(jsp_page).forward(request, response);        
    }

    @Override
    protected void doIdmCount(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            int user_number = idmProc.getRoletypeCount();
            out.print(user_number);
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }
}
