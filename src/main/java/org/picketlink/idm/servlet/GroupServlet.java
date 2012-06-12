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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.servlet.bean.GroupBean;
import org.picketlink.idm.servlet.processor.IdmProcessor;

/**
 *
 * @author vrockai
 */
public class GroupServlet extends IdmCrudServlet {

    private static Logger log = Logger.getLogger(GroupServlet.class.getName());
    private static final long serialVersionUID = 331231441234123441L;

    @Override
    public void init() throws ServletException {
        this.jsp_page = "groupList.jsp";
    }

    @Override
    protected void doIdmDefault(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        log.trace("Group default");
        int uAction = getIntParameter(request, "a", ACTION);
        
        log.trace("Group actions: " + uAction);

        // Breadcrumbs
        if (uAction == 6) {
            doBreadCrumbs(request, response, idmProc);
        } else if (uAction == 7) {
            browse(request, response, idmProc, "groupSelectList.jsp");
        }
        // Associate  group
        else if (uAction == 8) {
            doAsscociateGroup(request, response, idmProc);
         // deassociate group    
        } else if (uAction == 9) {
            doDisasscociateGroup(request, response, idmProc);
        }
    }
    
    private void doAsscociateGroup(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws IOException{
    	log.info("doing ass");
    	String pId = getStringParameter(request, "pId", "");
        String pT = getStringParameter(request, "pT", "");
        String gId = getStringParameter(request, "gId", "");
        String gT = getStringParameter(request, "gT", "");

        PrintWriter out = response.getWriter();
        try {
            idmProc.associateGroup(pId, pT, gId, gT);
            out.print(1);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }
    
    private void doDisasscociateGroup(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws IOException {
    	log.info("doing deass");
    	String pId = getStringParameter(request, "pId", "");
        String pT = getStringParameter(request, "pT", "");
        String gId = getStringParameter(request, "gId", "");
        String gT = getStringParameter(request, "gT", "");

        log.trace("deasssss: " + pId + ", " + pT + ", " + gId + ", " + gT);

        PrintWriter out = response.getWriter();
        try {
            idmProc.deassociateGroup(pId, pT, gId, gT);
            out.print(1);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }
    
    private void doBreadCrumbs(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws IOException, ServletException{
    	log.info("doing bread");
    	String gType = getStringParameter(request, "t", "");
        String gParent = getStringParameter(request, "p", "");

        try {
            Collection<GroupBean> parentList = new ArrayList<GroupBean>();

            if (!"".equals(gParent)) {
                parentList.addAll(getGroupBeanList(idmProc.getGroupParents(gParent, gType)));
                Group group = idmProc.getGroup(gParent, gType);
                log.trace("gP:" + gParent + ";gT:" + gType + ";g=" + group);
                parentList.add(new GroupBean(group));
            }

            request.getSession().setAttribute("parentList", parentList);

        } catch (Exception e) {
            log.error(e);
        } 
        
        request.getRequestDispatcher("groupCrumbs.jsp").forward(request, response);
    }

    @Override
    protected void doIdmAdd(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String gType = getStringParameter(request, "t", "");
        String gId = getStringParameter(request, "gId", "");

        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html");
            log.trace("Want to add group:" + gId + ", " + gType);
            idmProc.createGroup(gId, gType);
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
        String gType = getStringParameter(request, "t", "");
        String gId = getStringParameter(request, "gId", "");

        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html");
            log.trace("Want to delete group:" + gId + ", " + gType);
            idmProc.deleteGroup(gId, gType);
            out.print(1);
        } catch (Exception e) {
            log.error(e);
            out.print(0);
        } finally {
            out.close();
        }
    }

    @Override
    protected void doIdmEdit(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void doIdmBrowse(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        log.info("Doing Group Browse");
    	browse(request, response, idmProc, jsp_page);
    }

    private void browse(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc, String page) throws ServletException, IOException {
        int uPerPage = getIntParameter(request, "uPerPage", PERPAGE);
        int uOffset = getIntParameter(request, "uO", OFFSET) * uPerPage;
        String uQuery = getStringParameter(request, "q", QUERY);
        String gType = getStringParameter(request, "t", "root_type");
        String gParent = getStringParameter(request, "p", "");

        log.trace("Get group browse for '" + gParent + "' ('" + gType + "')");

        Collection<GroupBean> groupList;

        if ("".equals(gParent)) {
            log.trace("without parent");
            groupList = idmProc.getGroupsByRange(uOffset, uPerPage, uQuery, gType);
        } else {
            log.trace("with parent");
            groupList = idmProc.getGroupChildrenByRange(uOffset, uPerPage, uQuery, gParent, gType);
        }

        log.trace("group sizezeze: " + groupList.size());

        request.getSession().setAttribute("groupList", groupList);
        response.setContentType("text/html");
        request.getRequestDispatcher(page).forward(request, response);
    }

    @Override
    protected void doIdmCount(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        String gGroup = getStringParameter(request, "g", "");
        String gParent = getStringParameter(request, "p", "");
        String gType = getStringParameter(request, "t", "");

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        try {
            int groupNumber = 0;
            if (!"".equals(gParent)) {
                groupNumber = idmProc.getGroupChildrenCount(gParent, gType);
            } else if (!"".equals(gGroup)) {
                groupNumber = idmProc.getGroupCount(gGroup, gType);
            } else {
                groupNumber = idmProc.getGroupCount();
            }

            out.print(groupNumber);
        } catch (Exception e) {
            log.error(e);
            out.print(e.getMessage());
        } finally {
            out.close();
        }
    }

    private Collection<GroupBean> getGroupBeanList(Collection<Group> colg) {
        Collection<GroupBean> res = new ArrayList<GroupBean>();

        for (Group g : colg) {
            res.add(new GroupBean(g));
        }

        return res;
    }
}
