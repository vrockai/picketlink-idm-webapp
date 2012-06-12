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
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.servlet.bean.RoleBean;
import org.picketlink.idm.servlet.processor.IdmProcessor;

/**
 *
 * @author vrockai
 */
public class RoleServlet extends IdmCrudServlet {

    private static Logger log = Logger.getLogger(RoleServlet.class.getName());
    private static final long serialVersionUID = 331231441234123441L;

    @Override
    protected void doIdmAdd(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {

            String uId = request.getParameter("uId");
            String gId = request.getParameter("gId");
            String rtId = request.getParameter("rtId");

            log.trace("Add role - user: " + uId + ", group: " + gId + ", roletype: " + rtId);

            idmProc.associateRole(rtId, uId, gId);
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
            String uId = request.getParameter("uId");
            String gId = request.getParameter("gId");
            String rtId = request.getParameter("rtId");

            log.trace("Deletes role - user: " + uId + ", group: " + gId + ", roletype: " + rtId);

            idmProc.deassociateRole(rtId, uId, gId);
            out.print("1");
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmBrowse(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        int uPerPage = getIntParameter(request, "uPerPage", PERPAGE);
        int uOffset = getIntParameter(request, "uO", OFFSET) * uPerPage;
        String uQuery = getStringParameter(request, "q", QUERY);
        
        String rUser = getStringParameter(request, "u", "");
        String rGroup = getStringParameter(request, "g", "");
        String rGroupType = getStringParameter(request, "t", "");
        String rRoletype = getStringParameter(request, "rt", "");        
        
        Collection<RoleBean> roleBeanCol = new ArrayList<RoleBean>();

        try {
            if (!"".equals(rUser)) {
                roleBeanCol.addAll(idmProc.getRolesForUser(rUser));
            } else if (!"".equals(rGroup)) {
                log.trace("getting roles for '"+rGroup+"' '"+rGroupType+"'");
                roleBeanCol.addAll(idmProc.getRolesForGroup(rGroup, rGroupType));
            } else if (!"".equals(rRoletype)) {
                roleBeanCol.addAll(idmProc.getRolesForRoletype(rRoletype));
            } else {
                log.trace("Adding roles for uO"+uOffset+", uPP"+uPerPage+", uQ"+uQuery);
                roleBeanCol.addAll(idmProc.getRolesByRange(uOffset, uPerPage, uQuery));
            }
        } catch (FeatureNotSupportedException ex) {
            log.error(ex);
        }
        
        request.getSession().setAttribute("roleBeanCol", roleBeanCol);
        request.getRequestDispatcher("roleList.jsp").forward(request, response);
    }

    @Override
    protected void doIdmCount(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String rUser = getStringParameter(request, "u", "");
        String rGroup = getStringParameter(request, "g", "");
        String rGroupType = getStringParameter(request, "t", "");
        String rRoletype = getStringParameter(request, "rt", "");     
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            int roleCount = 0;
            
            if (!"".equals(rUser)) {
                roleCount = idmProc.getRolesForUser(rUser).size();
            } else if (!"".equals(rGroup)) {
                log.trace("getting roles for '"+rGroup+"' '"+rGroupType+"'");
                roleCount = idmProc.getRolesForGroup(rGroup, rGroupType).size();
            } else if (!"".equals(rRoletype)) {
                roleCount = idmProc.getRolesForRoletype(rRoletype).size();
            } else {
                roleCount = idmProc.getRoleCount();
            }
            
            log.trace("role_count:"+roleCount);
            out.print(roleCount);
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }
}
