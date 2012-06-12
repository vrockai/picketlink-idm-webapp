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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.picketlink.idm.servlet.processor.IdmProcessor;
import org.picketlink.idm.servlet.processor.IdmProcessorFactory;

/**
 *
 * @author vrockai
 */
abstract class IdmCrudServlet extends IdmBasicServlet {

    private static final long serialVersionUID = 331412312312431L;
    private static Logger log = Logger.getLogger(IdmCrudServlet.class.getName());

    protected String jsp_page;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();
        String customPage = jsp_page;
        
        int action = getIntParameter(request, "a", ACTION);
        
        try {
            switch (action) {
                case 0:
                    doIdmBrowse(request, response, idmProc);
                    break;
                case 1:
                    doIdmAdd(request, response, idmProc);
                    break;
                case 2:
                    doIdmDelete(request, response, idmProc);
                    break;
                case 3:
                    doIdmEdit(request, response, idmProc);
                    break;
                case 5:
                    doIdmCount(request, response, idmProc);
                    break;
                default:
                    doIdmDefault(request, response, idmProc);
                    break;
            }
       } catch (Exception e) {
            log.error(e);
            log.error(e.getCause());
            log.error(e.getLocalizedMessage());
            log.error(e.getStackTrace());
            e.printStackTrace();
       }

        log.trace("Servlet is using: " + customPage);
    }

    protected void doIdmDefault(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {
        response.setContentType("text/html");
        request.getRequestDispatcher(jsp_page).forward(request, response);
    }

    protected void doIdmAdd(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {};

    protected void doIdmDelete(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {};

    protected void doIdmEdit(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {};

    protected abstract void doIdmBrowse(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception;

    protected void doIdmCount(HttpServletRequest request, HttpServletResponse response, IdmProcessor idmProc) throws Exception {};
}
