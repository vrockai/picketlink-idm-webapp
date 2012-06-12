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

package org.picketlink.idm.servlet.processor;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

/**
 *
 * @author vrockai
 */
@Singleton
public class IdmProcessorFactory {

    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(IdmProcessorFactory.class.getName());

    private IdmProcessorFactory() {
        
    }
    
    private static class IdmProcessorHolder {
        private static final IdmProcessor idmProc = new IdmProcessor();
    }
    
    public static IdmProcessor getIdmProcessor() {
        return IdmProcessorHolder.idmProc;
    }

    /*
    public synchronized static IdmProcessor getIdmProcessor() {

        if (idmProc == null) {
            try {
                idmProc = new IdmProcessor();
                idmProc.initializeDB();
            } catch (IdentityConfigurationException ex) {
                Logger.getLogger(IdmServlet.class.getName()).error(ex);
            } catch (IdentityException ex) {
                Logger.getLogger(IdmServlet.class.getName()).error(ex);
            }
        }

        return idmProc;
    }    
    */
}