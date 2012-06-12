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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author vrockai
 */
abstract class IdmBasicServlet extends HttpServlet {

    private static final long serialVersionUID = 331412312312431L;
    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(IdmBasicServlet.class.getName());
	    
    // Default paginator values
    protected int PERPAGE = 5;
    protected int OFFSET = 0;
    protected String QUERY = "";
    
    // uAction : 0(default) - list, 1 - add, 2 - delete, 3 - edit, 4 - change password, 5 - max entries
    protected int ACTION = 0;

    protected String getStringParameter(HttpServletRequest request, String paramName, String defaultValue){
        return request.getParameter(paramName) != null ? request.getParameter(paramName) : defaultValue;        
    }
    
    protected int getIntParameter(HttpServletRequest request, String paramName, int defaultValue){
        return request.getParameter(paramName) != null ? Integer.valueOf(request.getParameter(paramName)) : defaultValue;        
    }
        
    @SuppressWarnings("ucd")
    protected boolean getBoolParameter(HttpServletRequest request, String paramName, boolean defaultValue){
        return request.getParameter(paramName) != null ? Boolean.valueOf(request.getParameter(paramName)) : defaultValue;
    }
}
