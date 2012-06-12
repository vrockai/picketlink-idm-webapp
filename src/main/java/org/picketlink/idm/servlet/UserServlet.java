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
import org.picketlink.idm.servlet.bean.UserBean;
import org.picketlink.idm.servlet.processor.IdmProcessor;

/**
 *
 * @author vrockai
 */
public class UserServlet extends IdmCrudServlet {

    private static Logger log = Logger.getLogger(UserServlet.class.getName());
    private static final long serialVersionUID = 331231441234123441L;

    @Override
    public void init() throws ServletException {
        this.jsp_page = "userList.jsp";
    }

    @Override
    protected void doIdmDefault(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        int uAction = getIntParameter(request, "a", ACTION);
        String userId = request.getParameter("uId");

        // Change password
        if (uAction == 7) {
            String uP1 = getStringParameter(request, "uP1", "");
            String uP2 = getStringParameter(request, "uP2", "");

            log.trace("Change user password: " + userId + ", p1: " + uP1 + ", p2:" + uP2);

            PrintWriter out = response.getWriter();
            try {
                idmProc.changeUserPassword(userId, uP1, uP2);
                out.print(1);
            } catch (Exception e) {
                log.error(e);
                out.print(e.getMessage());
            } finally {
                out.close();
            }
            // Associate to group
        } else if (uAction == 8) {
            String gId = getStringParameter(request, "gId", "");
            String gT = getStringParameter(request, "gT", "");

            PrintWriter out = response.getWriter();
            try {
                idmProc.associateUser(userId, gId, gT);
                out.print(1);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e);
                out.print(e.getMessage());
            } finally {
                out.close();
            }
            // deassociate from group
        } else if (uAction == 9) {
            String gId = getStringParameter(request, "gId", "");
            String gT = getStringParameter(request, "gT", "");

            PrintWriter out = response.getWriter();
            try {
                idmProc.deassociateUser(userId, gId, gT);
                out.print(1);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e);
                out.print(e.getMessage());
            } finally {
                out.close();
            }
        }
    }

    @Override
    protected void doIdmAdd(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            String userId = request.getParameter("uId");
            String fname = request.getParameter("uFn");
            String lname = request.getParameter("uLn");
            String email = request.getParameter("uEm");
            
            log.trace("Add user: " + userId);

            idmProc.createUser(userId);
            idmProc.createAttribute(userId, "firstName", fname);
            idmProc.createAttribute(userId, "lastName", lname);
            idmProc.createAttribute(userId, "email", email);
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
            String userId = request.getParameter("uId");
            idmProc.deleteUser(userId);
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
        String userId = request.getParameter("uId");
        String fname = request.getParameter("uFn");
        String lname = request.getParameter("uLn");
        String email = request.getParameter("uEm");

        log("Edit user: " + userId + ", " + fname + ", " + lname + ", " + email);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {


            idmProc.editAttributeValue(userId, "firstName", fname);
            idmProc.editAttributeValue(userId, "lastName", lname);
            idmProc.editAttributeValue(userId, "email", email);
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

        Collection<UserBean> userPagiList = idmProc.getUsersByRange(uOffset, uPerPage, uQuery);

        request.getSession().setAttribute("userPagiList", userPagiList);
        response.setContentType("text/html");
        request.getRequestDispatcher(jsp_page).forward(request, response);
    }

    @Override
    protected void doIdmCount(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            int user_number = idmProc.getUserCount();
            out.print(user_number);
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }
}
