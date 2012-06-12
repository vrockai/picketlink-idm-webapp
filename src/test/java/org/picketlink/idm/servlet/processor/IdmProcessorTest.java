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

import java.util.Collection;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.picketlink.idm.common.exception.IdentityConfigurationException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.servlet.bean.GroupBean;

/**
 *
 * @author vrockai
 */
public class IdmProcessorTest extends TestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(IdmProcessorTest.class.getName());

    public void testAssociateGroup() throws IdentityConfigurationException, IdentityException {
        IdmProcessor idmProc = new IdmProcessor();

        String gpName = "GroupParentName";
        String gcName = "GroupChildName";
        String gType = "root_type";

        idmProc.createGroup(gpName);
        idmProc.createGroup(gcName);

        idmProc.associateGroup(gpName, gType, gcName, gType);
        Collection<GroupBean> gbCol = idmProc.getGroupsByRange(0, 10, "*");

        for (GroupBean gb : gbCol) {
            if (gb.getName().equals(gcName)) {
                assertEquals(gb.getChildrenCount(), 0);
            }
            if (gb.getName().equals(gpName)) {
                assertEquals(gb.getChildrenCount(), 1);
                assertEquals(gb.getChildren().iterator().next().getName(), gcName);
            }
        }
    }
}
