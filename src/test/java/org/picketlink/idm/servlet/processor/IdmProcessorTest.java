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

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import static org.junit.Assert.*;
import org.junit.Test;
import org.picketlink.idm.api.*;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.servlet.bean.GroupBean;
import org.picketlink.idm.servlet.bean.RoleBean;
import org.picketlink.idm.servlet.bean.UserBean;

/**
 *
 * @author vrockai
 */
public class IdmProcessorTest {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(IdmProcessorTest.class.getName());
    IdmProcessor idmProc = new IdmProcessor();

    ////////////////////////////////////////
    @Test
    public void testUploadPicture() throws IOException, IdentityException {
        log.info("testUploadPicture");

        String userId = "photoUser";
        InputStream is = getClass().getClassLoader().getResourceAsStream("test.jpg");

        byte[] originalPictureBytes = IOUtils.toByteArray(is);

        is.read(originalPictureBytes);

        idmProc.beginTransaction();

        User u = idmProc.createUser(userId);

        Attribute a = idmProc.getAttributes(u).get("picture");

        assertNull(a);

        idmProc.uploadPicture(userId, originalPictureBytes);
        a = idmProc.getAttributes(u).get("picture");

        assertNotNull(a);
        assertTrue(a.getValue() instanceof byte[]);

        byte[] pictureBytes = (byte[]) a.getValue();

        assertEquals(originalPictureBytes.length, pictureBytes.length);
        assertEquals(originalPictureBytes, pictureBytes);

        idmProc.commitTransaction();
    }

    @Test
    public void testFindUser() throws IdentityException, UnsupportedCriterium {
        log.info("testFindUser");

        String userId1 = "findUserMock";
        String userId2 = "findUserViliam";
        String term1 = "findUser";
        String term2 = "i";

        idmProc.beginTransaction();

        // With empty database

        Collection<UserBean> userCol = idmProc.findUser(userId1);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findUser(userId2);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findUser(term1);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findUser(term2);

        assertTrue(userCol.isEmpty());

        // With single user in database

        idmProc.createUser(userId1);

        userCol = idmProc.findUser(userId1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findUser(userId2);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findUser(term1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findUser(term2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        // With two users in database

        idmProc.createUser(userId2);

        userCol = idmProc.findUser(userId1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findUser(userId2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findUser(term1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 2);

        userCol = idmProc.findUser(term2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 2);

        idmProc.commitTransaction();
    }

    @Test
    public void testFindGroup() throws IdentityException, UnsupportedCriterium {
        log.info("testFindGroup");

        String groupId1 = "findGroupMock";
        String groupId2 = "findGroupViliam";
        String term1 = "findGroup";
        String term2 = "i";

        idmProc.beginTransaction();

        // With empty database

        Collection<GroupBean> userCol = idmProc.findGroup(groupId1);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findGroup(groupId2);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findGroup(term1);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findGroup(term2);

        assertTrue(userCol.isEmpty());

        // With single user in database

        idmProc.createGroup(groupId1);

        userCol = idmProc.findGroup(groupId1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findGroup(groupId2);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findGroup(term1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findGroup(term2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        // With two users in database

        idmProc.createGroup(groupId2);

        userCol = idmProc.findGroup(groupId1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findGroup(groupId2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findGroup(term1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 2);

        userCol = idmProc.findGroup(term2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 2);

        idmProc.commitTransaction();
    }

    @Test
    public void testFindRoletype() throws IdentityException, UnsupportedCriterium, FeatureNotSupportedException {
        log.info("testFindRoletype");

        String rtId1 = "findRtMock";
        String rtId2 = "findRtViliam";
        String term1 = "findRt";
        String term2 = "i";

        idmProc.beginTransaction();

        // With empty database

        Collection<RoleType> userCol = idmProc.findRoletype(rtId1);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findRoletype(rtId2);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findRoletype(term1);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findRoletype(term2);

        assertTrue(userCol.isEmpty());

        // With single user in database

        idmProc.createRoletype(rtId1);

        userCol = idmProc.findRoletype(rtId1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findRoletype(rtId2);

        assertTrue(userCol.isEmpty());

        userCol = idmProc.findRoletype(term1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findRoletype(term2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        // With two users in database

        idmProc.createRoletype(rtId2);

        userCol = idmProc.findRoletype(rtId1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findRoletype(rtId2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 1);

        userCol = idmProc.findRoletype(term1);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 2);

        userCol = idmProc.findRoletype(term2);

        assertFalse(userCol.isEmpty());
        assertEquals(userCol.size(), 2);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetUserCount() throws IdentityException {
        log.info("testGetUserCount");

        String userId1 = "userCount1";
        String userId2 = "userCount2";

        idmProc.beginTransaction();

        int userCount = idmProc.getUserCount();

        assertEquals(userCount, 0);

        idmProc.createUser(userId1);
        userCount = idmProc.getUserCount();

        assertEquals(userCount, 1);

        idmProc.createUser(userId2);
        userCount = idmProc.getUserCount();

        assertEquals(userCount, 2);

        idmProc.deleteUser(userId1);
        userCount = idmProc.getUserCount();

        assertEquals(userCount, 1);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetRoletypeCount() throws IdentityException, FeatureNotSupportedException {
        log.info("testGetUserCount");

        String rtId1 = "userCount1";
        String rtId2 = "userCount2";

        idmProc.beginTransaction();

        int rtCount = idmProc.getRoletypeCount();

        assertEquals(rtCount, 0);

        idmProc.createRoletype(rtId1);
        rtCount = idmProc.getRoletypeCount();

        assertEquals(rtCount, 1);

        idmProc.createRoletype(rtId2);
        rtCount = idmProc.getRoletypeCount();

        assertEquals(rtCount, 2);

        idmProc.deleteRoletype(rtId1);
        rtCount = idmProc.getRoletypeCount();

        assertEquals(rtCount, 1);

        idmProc.commitTransaction();
    }

    @Test
    public void getGroupCount() throws IdentityException {
        log.info("getGroupCount");

        String groupId1 = "groupCount1";
        String groupId2 = "groupCount2";

        idmProc.beginTransaction();

        int groupCount = idmProc.getGroupCount();

        assertEquals(groupCount, 0);

        idmProc.createGroup(groupId1);
        groupCount = idmProc.getGroupCount();

        assertEquals(groupCount, 1);

        idmProc.createGroup(groupId2);
        groupCount = idmProc.getGroupCount();

        assertEquals(groupCount, 2);

        idmProc.deleteGroup(groupId1);
        groupCount = idmProc.getGroupCount();

        assertEquals(groupCount, 1);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetGroupChildrenCount() throws IdentityException {
        log.info("testGetGroupChildrenCount");

        String groupIdParent = "groupParent";

        String groupId1 = "groupCount1";
        String groupId2 = "groupCount2";

        idmProc.beginTransaction();

        idmProc.createGroup(groupIdParent);
        idmProc.createGroup(groupId1);
        idmProc.createGroup(groupId2);

        int childrenCount = idmProc.getGroupChildrenCount(groupIdParent, IdmProcessor.GROUP);
        assertEquals(childrenCount, 0);

        childrenCount = idmProc.getGroupChildrenCount(groupId1, IdmProcessor.GROUP);
        assertEquals(childrenCount, 0);

        childrenCount = idmProc.getGroupChildrenCount(groupId2, IdmProcessor.GROUP);
        assertEquals(childrenCount, 0);

        idmProc.associateGroup(groupIdParent, IdmProcessor.GROUP, groupId1, IdmProcessor.GROUP);
        childrenCount = idmProc.getGroupChildrenCount(groupIdParent, IdmProcessor.GROUP);
        assertEquals(childrenCount, 1);

        idmProc.associateGroup(groupIdParent, IdmProcessor.GROUP, groupId2, IdmProcessor.GROUP);
        childrenCount = idmProc.getGroupChildrenCount(groupIdParent, IdmProcessor.GROUP);
        assertEquals(childrenCount, 2);

        idmProc.deassociateGroup(groupIdParent, IdmProcessor.GROUP, groupId1, IdmProcessor.GROUP);
        childrenCount = idmProc.getGroupChildrenCount(groupIdParent, IdmProcessor.GROUP);
        assertEquals(childrenCount, 1);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetGroupCountByType() throws IdentityException {
        String groupId1 = "getGcount1";
        String groupId2 = "getGcount2";
        String groupId3 = "getGcount3";

        String groupType1 = "gType1";
        String groupType2 = "gType2";
        idmProc.beginTransaction();

        int groupT1count = idmProc.getGroupCount(groupType1);
        int groupT2count = idmProc.getGroupCount(groupType2);

        assertEquals(groupT1count, 0);
        assertEquals(groupT2count, 0);

        idmProc.createGroup(groupId1, groupType1);

        groupT1count = idmProc.getGroupCount(groupType1);
        groupT2count = idmProc.getGroupCount(groupType2);

        assertEquals(groupT1count, 1);
        assertEquals(groupT2count, 0);

        idmProc.createGroup(groupId2, groupType2);

        groupT1count = idmProc.getGroupCount(groupType1);
        groupT2count = idmProc.getGroupCount(groupType2);

        assertEquals(groupT1count, 1);
        assertEquals(groupT2count, 1);

        idmProc.createGroup(groupId3, groupType2);

        groupT1count = idmProc.getGroupCount(groupType1);
        groupT2count = idmProc.getGroupCount(groupType2);

        assertEquals(groupT1count, 1);
        assertEquals(groupT2count, 2);

        idmProc.deleteGroup(groupId3, groupType2);

        groupT1count = idmProc.getGroupCount(groupType1);
        groupT2count = idmProc.getGroupCount(groupType2);

        assertEquals(groupT1count, 1);
        assertEquals(groupT2count, 1);

        idmProc.deleteGroup(groupId1, groupType1);

        groupT1count = idmProc.getGroupCount(groupType1);
        groupT2count = idmProc.getGroupCount(groupType2);

        assertEquals(groupT1count, 0);
        assertEquals(groupT2count, 1);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetUser() throws IdentityException {
        String userId = "getUser";

        idmProc.beginTransaction();

        UserBean ub = idmProc.getUser(userId);
        assertNull(ub);

        idmProc.createUser(userId);

        ub = idmProc.getUser(userId);
        assertNotNull(ub);
        assertEquals(userId, ub.getUserId());

        idmProc.deleteUser(userId);
        ub = idmProc.getUser(userId);
        assertNull(ub);

        idmProc.commitTransaction();
    }

    @Test
    public void testAssociateGroup() throws IdentityException {
        idmProc.beginTransaction();
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
        idmProc.commitTransaction();

    }

    @Test
    public void testGetUsersByRange() throws IdentityException {
        String userId1 = "usersRange1x";
        String userId2 = "usersRange2y";
        String userId3 = "usersRange3z";

        idmProc.beginTransaction();

        Collection<UserBean> ubCol = idmProc.getUsersByRange(0, 4, "*");
        assertTrue(ubCol.isEmpty());

        idmProc.createUser(userId1);
        ubCol = idmProc.getUsersByRange(0, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.createUser(userId2);
        ubCol = idmProc.getUsersByRange(0, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 2);

        ubCol = idmProc.getUsersByRange(0, 1, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getUsersByRange(1, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.createUser(userId3);
        ubCol = idmProc.getUsersByRange(1, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 2);

        ubCol = idmProc.getUsersByRange(0, 4, "x");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getUsersByRange(1, 4, "x");
        assertTrue(ubCol.isEmpty());

        idmProc.deleteUser(userId3);
        ubCol = idmProc.getUsersByRange(1, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetGroupsByRange() throws IdentityException {
        String groupId1 = "grpRange1x";
        String groupId2 = "grpRange2y";
        String groupId3 = "grpRange3z";

        idmProc.beginTransaction();

        Collection<GroupBean> ubCol = idmProc.getGroupsByRange(0, 4, "*");
        assertTrue(ubCol.isEmpty());

        idmProc.createGroup(groupId1);
        ubCol = idmProc.getGroupsByRange(0, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.createGroup(groupId2);
        ubCol = idmProc.getGroupsByRange(0, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 2);

        ubCol = idmProc.getGroupsByRange(0, 1, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(1, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.createGroup(groupId3);
        ubCol = idmProc.getGroupsByRange(1, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 2);

        ubCol = idmProc.getGroupsByRange(0, 4, "x");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(1, 4, "x");
        assertTrue(ubCol.isEmpty());

        idmProc.deleteGroup(groupId3);
        ubCol = idmProc.getGroupsByRange(1, 4, "*");
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.commitTransaction();
    }

    @Test
    public void testGetGroupsByRangeByType() throws IdentityException {
        String groupId1 = "grpRange1x";
        String groupId2 = "grpRange2y";
        String groupId3 = "grpRange3z";

        String groupType1 = "grpType1";
        String groupType2 = "grpType2";

        idmProc.beginTransaction();

        Collection<GroupBean> ubCol = idmProc.getGroupsByRange(0, 4, "*", groupType1);
        assertTrue(ubCol.isEmpty());

        idmProc.createGroup(groupId1, groupType1);
        ubCol = idmProc.getGroupsByRange(0, 4, "*", groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        idmProc.createGroup(groupId2, groupType2);
        ubCol = idmProc.getGroupsByRange(0, 4, "*", groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(0, 4, "*", groupType2);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(0, 1, "*", groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(0, 1, "*", groupType2);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(1, 4, "*", groupType1);
        assertTrue(ubCol.isEmpty());

        ubCol = idmProc.getGroupsByRange(1, 4, "*", groupType2);
        assertTrue(ubCol.isEmpty());

        idmProc.createGroup(groupId3, groupType1);
        ubCol = idmProc.getGroupsByRange(1, 4, "*", groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(0, 4, "x", groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(ubCol.size(), 1);

        ubCol = idmProc.getGroupsByRange(1, 4, "x", groupType1);
        assertTrue(ubCol.isEmpty());

        idmProc.deleteGroup(groupId3, groupType1);
        ubCol = idmProc.getGroupsByRange(1, 4, "*", groupType1);
        assertTrue(ubCol.isEmpty());

        idmProc.deleteGroup(groupId2, groupType2);
        ubCol = idmProc.getGroupsByRange(1, 4, "*", groupType2);
        assertTrue(ubCol.isEmpty());

        idmProc.commitTransaction();
    }

    @Test
    public void testCreateDeleteUser() throws IdentityException, UnsupportedCriterium {
        String userId1 = "userId1";
        String userId2 = "userId2";

        String term = "userId";

        idmProc.beginTransaction();

        Collection<UserBean> ubCol = idmProc.findUser(userId1);
        assertTrue(ubCol.isEmpty());

        idmProc.createUser(userId1);
        ubCol = idmProc.findUser(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.createUser(userId2);
        ubCol = idmProc.findUser(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(2, ubCol.size());

        idmProc.deleteUser(userId1);
        ubCol = idmProc.findUser(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.commitTransaction();
    }

    @Test
    public void testCreateDeleteGroup() throws IdentityException, UnsupportedCriterium {
        String groupId1 = "grpId1";
        String groupId2 = "grpId2";

        String term = "grpId";

        idmProc.beginTransaction();

        Collection<GroupBean> ubCol = idmProc.findGroup(groupId1);
        assertTrue(ubCol.isEmpty());

        idmProc.createGroup(groupId1);
        ubCol = idmProc.findGroup(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.createGroup(groupId2);
        ubCol = idmProc.findGroup(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(2, ubCol.size());

        idmProc.deleteGroup(groupId1);
        ubCol = idmProc.findGroup(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.commitTransaction();
    }

    @Test
    public void testCreateDeleteGetAttribute() throws IdentityException, UnsupportedCriterium {
        String userId = "userId";

        String attId1 = "attId1";
        String attId2 = "attId2";

        idmProc.beginTransaction();

        User user = idmProc.createUser(userId);
        Attribute a = idmProc.getAttribute(attId1, user);
        assertNull(a);

        idmProc.createAttribute(userId, attId1, attId1+"val");
        a = idmProc.getAttribute(attId1, user);
        assertNotNull(a);
        assertEquals(attId1,a.getName());
        assertEquals(attId1+"val",String.valueOf(a.getValue()));                
        
        idmProc.createAttribute(userId, attId2, attId2+"val");
        a = idmProc.getAttribute(attId2, user);
        assertNotNull(a);
        assertEquals(attId2,a.getName());
        assertEquals(attId2+"val",String.valueOf(a.getValue()));                

        idmProc.deleteAttribute(userId, attId1);
        a = idmProc.getAttribute(attId1, user);
        assertNull(a);
        a = idmProc.getAttribute(attId2, user);
        assertNotNull(a);

        idmProc.commitTransaction();
    }
    
    @Test
    public void testEditAttribute() throws IdentityException, UnsupportedCriterium {
        String userId = "userId";

        String attId1 = "attId1";
        String attId2 = "attId2";
        
        String val1 = "val1";
        String val2 = "val1";

        idmProc.beginTransaction();

        User user = idmProc.createUser(userId);
        Attribute a = idmProc.getAttribute(attId1, user);
        assertNull(a);
        a = idmProc.getAttribute(attId2, user);
        assertNull(a);

        idmProc.createAttribute(userId, attId1, val1);
        a = idmProc.getAttribute(attId1, user);
        assertNotNull(a);
        assertEquals(attId1,a.getName());
        assertEquals(val1,String.valueOf(a.getValue()));                
        a = idmProc.getAttribute(attId2, user);
        assertNull(a);
        
        // change value
        idmProc.editAttribute(userId, attId1, null, attId1, val2);
        a = idmProc.getAttribute(attId1, user);
        assertNotNull(a);
        assertEquals(attId1,a.getName());
        assertEquals(val2,String.valueOf(a.getValue()));                
        a = idmProc.getAttribute(attId2, user);
        assertNull(a);
        
        // rename attribute
        idmProc.editAttribute(userId, attId1, val2, attId2, val1);
        a = idmProc.getAttribute(attId1, user);
        assertNull(a);
        a = idmProc.getAttribute(attId2, user);
        assertNotNull(a);
        assertEquals(attId2,a.getName());
        assertEquals(val1,String.valueOf(a.getValue()));                        

        idmProc.commitTransaction();
    }

    @Test
    public void testCreateDeleteGroupByType() throws IdentityException, UnsupportedCriterium {
        String groupId1 = "grpId1";
        String groupId2 = "grpId2";

        String groupType1 = "grpt1";
        String groupType2 = "grpt2";

        String term = "grpId";

        idmProc.beginTransaction();

        Collection<GroupBean> ubCol = idmProc.findGroup(term, groupType1);
        assertTrue(ubCol.isEmpty());
        ubCol = idmProc.findGroup(term, groupType2);
        assertTrue(ubCol.isEmpty());

        idmProc.createGroup(groupId1, groupType1);
        ubCol = idmProc.findGroup(term, groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());
        ubCol = idmProc.findGroup(term, groupType2);
        assertTrue(ubCol.isEmpty());
        assertEquals(0, ubCol.size());

        idmProc.createGroup(groupId2, groupType2);
        ubCol = idmProc.findGroup(term, groupType1);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());
        ubCol = idmProc.findGroup(term, groupType2);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.deleteGroup(groupId1, groupType1);
        ubCol = idmProc.findGroup(term, groupType1);
        assertTrue(ubCol.isEmpty());
        assertEquals(0, ubCol.size());
        ubCol = idmProc.findGroup(term, groupType2);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.commitTransaction();
    }

    @Test
    public void testCreateDeleteRoletype() throws IdentityException, UnsupportedCriterium, FeatureNotSupportedException {
        String rtId1 = "grpId1";
        String rtId2 = "grpId2";

        String term = "grpId";

        idmProc.beginTransaction();

        Collection<RoleType> ubCol = idmProc.findRoletype(rtId1);
        assertTrue(ubCol.isEmpty());

        idmProc.createRoletype(rtId1);
        ubCol = idmProc.findRoletype(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.createRoletype(rtId2);
        ubCol = idmProc.findRoletype(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(2, ubCol.size());

        idmProc.deleteRoletype(rtId1);
        ubCol = idmProc.findRoletype(term);
        assertFalse(ubCol.isEmpty());
        assertEquals(1, ubCol.size());

        idmProc.commitTransaction();
    }

    @Test
    public void testGetGroup() throws IdentityException {

        String grpId1 = "grpId1";
        String grpId2 = "grpId2";

        String grpType1 = "grpType1";
        String grpType2 = "grpType2";

        idmProc.beginTransaction();

        Group group = idmProc.getGroup(grpId1, grpType1);
        assertNull(group);

        group = idmProc.getGroup(grpId1, grpType2);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType1);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType2);
        assertNull(group);


        idmProc.createGroup(grpId1, grpType1);
        group = idmProc.getGroup(grpId1, grpType1);
        assertNotNull(group);
        assertEquals(grpId1, group.getName());

        group = idmProc.getGroup(grpId1, grpType2);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType1);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType2);
        assertNull(group);


        idmProc.createGroup(grpId2, grpType2);
        group = idmProc.getGroup(grpId1, grpType1);
        assertNotNull(group);
        assertEquals(grpId1, group.getName());

        group = idmProc.getGroup(grpId1, grpType2);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType1);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType2);
        assertNotNull(group);
        assertEquals(grpId2, group.getName());


        idmProc.deleteGroup(grpId2, grpType2);
        group = idmProc.getGroup(grpId1, grpType1);
        assertNotNull(group);
        assertEquals(grpId1, group.getName());

        group = idmProc.getGroup(grpId1, grpType2);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType1);
        assertNull(group);

        group = idmProc.getGroup(grpId2, grpType2);
        assertNull(group);

        idmProc.commitTransaction();
    }
    /*
     * @Test public UserBean getAttributesByRange(int offset, int numberOfRows,
     * String query, String user) throws Exception { throw new Exception("Not
     * yet implemented."); }
     *
     * @Test public int getAttributeCount(String user) throws Exception { throw
     * new Exception("Not yet implemented."); }
     *
     * @Test public Collection<GroupBean> getGroupChildrenByRange(int offset,
     * int numberOfRows, String query, String gParent, String gPType) throws
     * Exception { throw new Exception("Not yet implemented."); }
     *
     * @Test public Map<String, Attribute> getAttributes(IdentityType it) throws
     * Exception { throw new Exception("Not yet implemented."); }
     *
     * @Test public Collection<Group> getGroupParents(String g, String gType)
     * throws Exception { throw new Exception("Not yet implemented."); }
     *
     * @Test public void changeUserPassword(String userId, String p1, String p2)
     * throws IdentityException, Exception { throw new Exception("Not yet
     * implemented."); }
     *
     * @Test public void deassociateGroup(String pId, String pType, String gId,
     * String gType) throws IdentityException, Exception { throw new
     * Exception("Not yet implemented."); }
     *
     * @Test @SuppressWarnings("ucd") public void associateUser(String userId,
     * String groupId) throws IdentityException, Exception { throw new
     * Exception("Not yet implemented."); }
     *
     * @Test public void associateUser(String userId, String groupId, String
     * gType) throws IdentityException, Exception { throw new Exception("Not yet
     * implemented."); }
     *
     * @Test @SuppressWarnings("ucd") public void deassociateUser(String userId,
     * String groupId) throws IdentityException, Exception { throw new
     * Exception("Not yet implemented."); }
     *
     * @Test public void deassociateUser(String userId, String groupId, String
     * gType) throws IdentityException, Exception { throw new Exception("Not yet
     * implemented."); }
     *
     * @Test public void editAttributeValue(String userId, String attName,
     * String attVal) throws IdentityException, Exception { throw new
     * Exception("Not yet implemented."); }
   
     * @Test public void associateRole(String roletype, String userId, String
     * groupName) throws IdentityException, FeatureNotSupportedException,
     * Exception { throw new Exception("Not yet implemented.");
     *
     * @Test public void deassociateRole(String roletype, String userId, String
     * groupName) throws IdentityException, FeatureNotSupportedException,
     * Exception { throw new Exception("Not yet implemented."); }
     *
     * @Test public Collection<RoleType> getRoletypesByRange(int offset, int
     * numberOfRows, String query) throws Exception { throw new Exception("Not
     * yet implemented.");
     *
     * @Test public int getRoleCount() throws Exception { throw new
     * Exception("Not yet implemented."); }
     *
     * @Test public Collection<? extends RoleBean> getRolesByRange(int offset,
     * int numberOfRows, String query) throws FeatureNotSupportedException,
     * Exception { throw new Exception("Not yet implemented."); }
     *
     * @Test public Collection<? extends RoleBean> getRolesForUser(String rUser)
     * throws FeatureNotSupportedException, Exception { throw new Exception("Not
     * yet implemented."); }
     *
     * @Test public Collection<? extends RoleBean> getRolesForGroup(String
     * rGroup, String gType) throws FeatureNotSupportedException, Exception {
     * throw new Exception("Not yet implemented."); }
     *
     * @Test public Collection<? extends RoleBean> getRolesForRoletype(String
     * rRoletype) throws FeatureNotSupportedException, Exception { throw new
     * Exception("Not yet implemented."); }
     *
     */
}
