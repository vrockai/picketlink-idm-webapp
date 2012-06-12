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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.UnsupportedCriterium;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.servlet.bean.GroupBean;
import org.picketlink.idm.servlet.bean.UserBean;
import org.picketlink.idm.servlet.processor.IdmProcessor;
import org.picketlink.idm.servlet.processor.IdmProcessorFactory;

/**
 *
 * @author vrockai
 */
public class AutocompleteServlet extends IdmBasicServlet {

    private static Logger log = Logger.getLogger(AutocompleteServlet.class.getName());
    private static final long serialVersionUID = 331424523451L;

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        PrintWriter out = response.getWriter();

        // type can be "u" for user, "g" for group and "r" for roletype.
        String type = getStringParameter(request, "type", "u");
        String term = getStringParameter(request, "term", "*");

        log.trace("Type: " + type);
        log.trace("Term: " + term);

        Collection<String> tipList = new ArrayList<String>();

        try {
            IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();
            // If looking for users
            if (type.contains("u")) {
                Collection<UserBean> userList = idmProc.findUser(term);

                for (UserBean ub : userList) {                 
                    tipList.add(ub.getUserId());
                }
            }
            // If looking for groups
            if (type.contains("g")) {
                Collection<GroupBean> groupList = idmProc.findGroup(term);

                for (GroupBean ub : groupList) {
                    tipList.add(ub.getName());
                }
            }
            // If looking for roletypes
            if (type.contains("r")) {
                Collection<RoleType> rtList = idmProc.findRoletype(term);

                for (RoleType ub : rtList) {
                    tipList.add(ub.getName());
                }
            }

        } catch (IdentityException ex) {
            log.error(ex);
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        }

        sb.append("[ ");
        Iterator<String> tipIter = tipList.iterator();
        do {
            sb.append("\"").append(tipIter.next()).append("\"");
            if (tipIter.hasNext()) {
                sb.append(",");
            }
        } while (tipIter.hasNext());
        sb.append("] ");

        log.trace("result: " + sb.toString());

        out.print(sb.toString());
        out.close();
    }
}
