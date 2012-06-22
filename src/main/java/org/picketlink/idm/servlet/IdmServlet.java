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
import org.apache.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.picketlink.idm.servlet.processor.IdmProcessor;
import org.picketlink.idm.servlet.processor.IdmProcessorFactory;

/**
 *
 * @author vrockai
 */
public class IdmServlet extends IdmBasicServlet {

    private static Logger log = Logger.getLogger(IdmServlet.class.getName());
    private static final long serialVersionUID = 33141L;
    private boolean firstStart = true;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        int populate = getIntParameter(request, "populate", 0);
        if (populate == 1){
            IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();
            PrintWriter out = response.getWriter();
            try {
                idmProc.initializeDB();
                out.print(1);
            } catch (Exception e) {
                log.error(e);
                e.printStackTrace();
                out.print(e.getMessage());
            } finally {
                out.close();
            }
        } else if (firstStart && isDBempty()) {
            firstStart = false;
            request.getRequestDispatcher("intro.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("main.jsp").forward(request, response);
        }
    }
    
    private boolean isDBempty() {
        IdmProcessor idmProc = IdmProcessorFactory.getIdmProcessor();

        int userCount = idmProc.getUserCount();
        int groupCount = idmProc.getGroupCount();
        int rtCount = idmProc.getRoletypeCount();
        
        log.info("Counts: "+userCount+", "+groupCount+", "+rtCount);

        return ((userCount == 0) && (groupCount == 0) && (rtCount == 0));
    }
}
