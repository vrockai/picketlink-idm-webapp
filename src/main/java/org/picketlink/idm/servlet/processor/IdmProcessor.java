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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.IdentitySessionFactory;
import org.picketlink.idm.api.IdentityType;
import org.picketlink.idm.api.Role;
import org.picketlink.idm.api.RoleManager;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.UnsupportedCriterium;
import org.picketlink.idm.api.User;
import org.picketlink.idm.api.cfg.IdentityConfiguration;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityConfigurationException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.SimpleAttribute;
import org.picketlink.idm.impl.configuration.IdentityConfigurationImpl;
import org.picketlink.idm.servlet.bean.AttributeBean;
import org.picketlink.idm.servlet.bean.GroupBean;
import org.picketlink.idm.servlet.bean.RoleBean;
import org.picketlink.idm.servlet.bean.UserBean;

/**
 *
 * @author vrockai
 */
public class IdmProcessor {

    private static Logger log = Logger.getLogger(IdmProcessor.class.getName());
    private IdentitySession identitySession = null;
    private IdentitySessionFactory identitySessionFactory = null;
    private String GROUP = "root_type";

    public IdmProcessor() {
        try {
        	init("picketlink-config.xml");
        } catch (Exception e) {
            log.error(e);
        }
    }

    @SuppressWarnings("ucd")
    public IdmProcessor(String configFile) throws IdentityConfigurationException, IdentityException {
    	try {
        	init(configFile);
        } catch (Exception e) {
            log.error(e);
        }	
    }

    private void init(String configFile) throws IdentityException {
    	IdentityConfiguration identityConfiguration = new IdentityConfigurationImpl().configure(configFile);
        Configuration cfg = new Configuration().setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect").setProperty("hibernate.connection.datasource", "java:comp/env/jdbc/test").setProperty("hibernate.order_updates", "true");
        identityConfiguration.getIdentityConfigurationRegistry().register(cfg, "hibernateSessionFactory");
        identitySessionFactory = identityConfiguration.buildIdentitySessionFactory();
        identitySession = identitySessionFactory.createIdentitySession("idm_realm");
    }

    @Deprecated
    @SuppressWarnings("ucd")
    public void initializeDB() {
        try {
            identitySession.beginTransaction();

            identitySession.getPersistenceManager().createUser("John Doe");
            User usr1 = identitySession.getPersistenceManager().createUser("Viliam Rockai");
            identitySession.getPersistenceManager().createUser("Prabhat Jha");
            identitySession.getPersistenceManager().createGroup("grupa1", GROUP);
            Group grp1 = identitySession.getPersistenceManager().createGroup("grupa2", GROUP);
            identitySession.getPersistenceManager().createGroup("pogrup", GROUP);
            identitySession.getRelationshipManager().associateUser(grp1, usr1);

            RoleManager roleManager = identitySession.getRoleManager();
            RoleType roletype = roleManager.createRoleType("new_roletype");

            roleManager.createRole(roletype, usr1, grp1);

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (Exception ex) {
            log.info(ex.getLocalizedMessage());
        }
    }
    
    private Collection<Group> getAssignedGroups(String username) {
        Collection<Group> groups = new ArrayList<Group>();
        try {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().findUser(username);
            groups = identitySession.getRelationshipManager().findAssociatedGroups(user);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }
        return groups;
    }

    public Collection<UserBean> findUser(String term) throws IdentityException, UnsupportedCriterium {

        Collection<UserBean> userBeanList = new ArrayList<UserBean>();
        Collection<User> users = new ArrayList<User>();
        try {
            identitySession.beginTransaction();

            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + term + "*");

            users = identitySession.getPersistenceManager().findUser(isc);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        for (User u : users) {
            UserBean ub = new UserBean(u);
            ub.setAttributes(convertAttributes(getAttributeCollection(u)));
            userBeanList.add(ub);
            ub.setAssociatedGroups(getAssignedGroups(u.getId()));
        }

        return userBeanList;

    }

    public Collection<GroupBean> findGroup(String term) throws IdentityException, UnsupportedCriterium {

        Collection<GroupBean> groups = new ArrayList<GroupBean>();

        try {
            identitySession.beginTransaction();

            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + term + "*");

            Collection<Group> groupList = identitySession.getPersistenceManager().findGroup(GROUP, isc);

            for (Group g : groupList) {

                GroupBean gb = new GroupBean(g);

                Collection<Role> rc = new ArrayList<Role>();
                try {
                    for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                        rc.addAll(identitySession.getRoleManager().findRoles(g, rt));
                    }
                } catch (FeatureNotSupportedException ex) {
                    log.error(ex);
                }
                gb.setRoleList(rc);

                gb.setChildren(getSubGroupCol(g));
                Collection<Group> plist = identitySession.getRelationshipManager().findAssociatedGroups(g, null, true, true);
                gb.setParentList(plist);

                groups.add(gb);
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }
        return groups;

    }

    public Collection<RoleType> findRoletype(String term) throws IdentityException, UnsupportedCriterium {

        Collection<RoleType> roletypeList = new ArrayList<RoleType>();
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + term + "*");
            roletypeList = roleManager.findRoleTypes(isc);
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        } catch (FeatureNotSupportedException ex) {
            log.error(ex);
        }

        return roletypeList;

    }

    public int getUserCount() {

        int result = 0;
        try {
            this.identitySession.beginTransaction();
            result = this.identitySession.getPersistenceManager().getUserCount();
            this.identitySession.getTransaction().commit();
            this.identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return result;

    }

    public int getGroupCount() {
        return getGroupCount(GROUP);
    }

    public int getGroupChildrenCount(String gParent, String gType) {
        int result = 0;
        try {
            this.identitySession.beginTransaction();
            Group parent = identitySession.getPersistenceManager().findGroup(gParent, gType);

            if (parent == null) {
                log.error("parent is null");
                return 0;
            }

            Collection<Group> childrenGroupCol = identitySession.getRelationshipManager().findAssociatedGroups(parent, null, true, false);

            result = childrenGroupCol.size();
            this.identitySession.getTransaction().commit();
            this.identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        log.trace("Get group count fun " + gParent + " (" + gType + ") = " + result);
        return result;
    }

    public int getGroupCount(String gGroup, String gType) {
        log.trace("Pocitam grupy: " + gGroup + ", type: " + gType);
        int result = 0;
        try {
            this.identitySession.beginTransaction();
            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + gGroup + "*");
            Collection<Group> grps = identitySession.getPersistenceManager().findGroup(gType, isc);
            result = grps.size();
            this.identitySession.getTransaction().commit();
            this.identitySession.close();
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return result;
    }

    private int getGroupCount(String gType) {
        int result = 0;
        try {
            this.identitySession.beginTransaction();
            result = this.identitySession.getPersistenceManager().getGroupTypeCount(gType);
            this.identitySession.getTransaction().commit();
            this.identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return result;
    }

    public Collection<UserBean> getUsersByRange(int offset, int numberOfRows, String query) {
        Collection<UserBean> userBeanList = new ArrayList<UserBean>();
        Collection<User> users = new ArrayList<User>();
        try {
            identitySession.beginTransaction();
            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + query + "*").page(offset, numberOfRows);
            users = identitySession.getPersistenceManager().findUser(isc);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (UnsupportedCriterium ex) {
            log.error(ex);

        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        for (User u : users) {
            UserBean ub = new UserBean(u);
            ub.setAttributes(convertAttributes(getAttributeCollection(u)));
            Attribute fname = getAttribute("firstName", u);
            ub.setFname(fname != null && fname.getValue() != null ? fname.getValue().toString() : "");
            Attribute lname = getAttribute("lastName", u);
            ub.setLname(lname != null && lname.getValue() != null ? lname.getValue().toString() : "");
            Attribute email = getAttribute("email", u);
            ub.setEmail(email != null && email.getValue() != null ? email.getValue().toString() : "");
            Attribute image = getAttribute("image", u);
            ub.setImage(image != null && image.getValue() != null ? image.getValue().toString() : "");
            userBeanList.add(ub);
            ub.setAssociatedGroups(getAssignedGroups(u.getId()));
        }

        return userBeanList;
    }

    public UserBean getAttributesByRange(int offset, int numberOfRows, String query, String user) {

        UserBean ub = null;
        try {
            identitySession.beginTransaction();

            User u = identitySession.getPersistenceManager().findUser(user);
            ub = new UserBean(u);

            identitySession.getTransaction().commit();
            identitySession.close();

            Collection<Attribute> atts = getAttributeCollection(u);

            List<Attribute> attsAlist = new ArrayList<Attribute>(atts);

            int min = offset;
            int max = offset + numberOfRows;

            if (attsAlist.isEmpty() || (attsAlist.size() < min)) {
                min = max = 0;
            } else if (attsAlist.size() < max) {
                max = attsAlist.size();
            }

            List<Attribute> attsAlistSub = attsAlist.subList(min, max);

            ub.setAttributes(convertAttributes(attsAlistSub));

        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return ub;
    }

    public int getAttributeCount(String user) {

        int size = 0;

        try {
            identitySession.beginTransaction();

            User u = identitySession.getPersistenceManager().findUser(user);

            identitySession.getTransaction().commit();
            identitySession.close();

            Collection<Attribute> atts = getAttributeCollection(u);

            size = atts.size();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return size;
    }

    @SuppressWarnings("ucd")
    public Collection<GroupBean> getGroupsByRange(int offset, int numberOfRows, String query) {
        return getGroupsByRange(offset, numberOfRows, query, GROUP);
    }

    public Collection<GroupBean> getGroupsByRange(int offset, int numberOfRows, String query, String gType) {

        log.trace("Getting groups: " + offset + ", " + numberOfRows + ", " + query + ", " + gType);

        Collection<GroupBean> groups = new ArrayList<GroupBean>();

        try {
            identitySession.beginTransaction();

            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + query + "*").page(offset, numberOfRows);

            Collection<Group> groupList = identitySession.getPersistenceManager().findGroup(gType, isc);

            for (Group g : groupList) {

                GroupBean gb = new GroupBean(g);

                Collection<Role> rc = new ArrayList<Role>();
                try {
                    for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                        rc.addAll(identitySession.getRoleManager().findRoles(g, rt));
                    }
                } catch (FeatureNotSupportedException ex) {
                    log.error(ex);
                }
                gb.setRoleList(rc);

                gb.setChildren(getSubGroupCol(g));
                Collection<Group> plist = identitySession.getRelationshipManager().findAssociatedGroups(g, gType, true, true);
                gb.setParentList(plist);

                groups.add(gb);
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }
        return groups;
    }

    public Collection<GroupBean> getGroupChildrenByRange(int offset, int numberOfRows, String query, String gParent, String gPType) {

        log.trace("Getting group children: " + offset + ", " + numberOfRows + ", " + query + ", " + gPType);

        Collection<GroupBean> groups = new ArrayList<GroupBean>();

        try {
            identitySession.beginTransaction();

            Group parent = identitySession.getPersistenceManager().findGroup(gParent, gPType);

            if (parent == null) {
                java.util.logging.Logger.getAnonymousLogger().fine("parent is null");
                return groups;
            }

            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + query + "*").page(offset, numberOfRows);
            Collection<Group> childrenGroupCol = identitySession.getRelationshipManager().findAssociatedGroups(parent, null, true, false, isc);

            for (Group g : childrenGroupCol) {
                GroupBean gb = new GroupBean(g);
                gb.setChildren(getSubGroupCol(g));
                groups.add(gb);
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }
        return groups;
    }

    private Collection<AttributeBean> convertAttributes(Collection<Attribute> cat) {
        Collection<AttributeBean> attList = new ArrayList<AttributeBean>();
        for (Attribute a : cat) {
            attList.add(new AttributeBean(a));
        }

        return attList;
    }

    private Attribute getAttribute(String attName, IdentityType it) {
        Attribute attList = null;

        try {
            identitySession.beginTransaction();
            AttributesManager attManager = identitySession.getAttributesManager();

            attList = attManager.getAttribute(it, attName);
            identitySession.getTransaction().commit();
            identitySession.close();

        } catch (IdentityException ex) {
            log.error(ex);
        }

        return attList;
    }

    private Collection<Attribute> getAttributeCollection(IdentityType it) {
        Collection<Attribute> attList = new ArrayList<Attribute>();

        try {
            identitySession.beginTransaction();
            AttributesManager attManager = identitySession.getAttributesManager();

            Map<String, Attribute> attributes = attManager.getAttributes(it);
            attList = attributes.values();

            identitySession.getTransaction().commit();
            identitySession.close();

        } catch (IdentityException ex) {
            log.error(ex);
        }

        return attList;
    }

    private Collection<GroupBean> getSubGroupCol(Group parent) throws IdentityException {
        Collection<GroupBean> result = new ArrayList<GroupBean>();

        Collection<Group> childrenGroupCol = identitySession.getRelationshipManager().findAssociatedGroups(parent, null, true, false);

        for (Group g : childrenGroupCol) {
            Collection<Group> ch = identitySession.getRelationshipManager().findAssociatedGroups(g, null, true, false);
            GroupBean gb = new GroupBean(g);

            Collection<GroupBean> chb = new ArrayList<GroupBean>();
            for (Group gch : ch) {
                chb.add(new GroupBean(gch));
            }

            gb.setChildren(chb);
            result.add(gb);
        }

        return result;
    }
/*
    public Collection<GroupBean> getAllChildrenGroups(String gParent, String gType) {
        Collection<GroupBean> groups = new ArrayList<GroupBean>();

        try {
            identitySession.beginTransaction();

            Group parent = identitySession.getPersistenceManager().findGroup(gParent, gType);

            if (parent == null) {
                java.util.logging.Logger.getAnonymousLogger().fine("parent is null");
                return groups;
            }

            Collection<Group> childrenGroupCol = identitySession.getRelationshipManager().findAssociatedGroups(parent, null, true, false);

            for (Group g : childrenGroupCol) {
                GroupBean gb = new GroupBean(g);
                gb.setChildren(getSubGroupCol(g));
                groups.add(gb);
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }
        return groups;
    }
*/
    public Group getGroup(String gname, String gType) {
        Group gParent = null;

        try {
            identitySession.beginTransaction();

            gParent = identitySession.getPersistenceManager().findGroup(gname, gType);

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return gParent;
    }

    public Collection<Group> getGroupParents(String g, String gType) {
        Collection<Group> groups = new ArrayList<Group>();

        try {
            identitySession.beginTransaction();

            Group gParent = identitySession.getPersistenceManager().findGroup(g, gType);

            if (gParent == null) {
                return groups;
            }

            groups = identitySession.getRelationshipManager().findAssociatedGroups(gParent, null, false, true);

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        log.trace("found " + groups.size() + " parents");

        return groups;
    }
    /*
     * public Collection<GroupBean> getAllGroups() { Collection<GroupBean>
     * groups = new ArrayList<GroupBean>(); try {
     *
     * identitySession.beginTransaction(); Collection<Group> groupList =
     * identitySession.getPersistenceManager().findGroup(GROUP,
     * (IdentitySearchCriteria) null);
     *
     * for (Group g : groupList) {
     *
     * GroupBean gb = new GroupBean(g);
     *
     * Collection<Role> rc = new ArrayList<Role>(); try { for (RoleType rt :
     * identitySession.getRoleManager().findRoleTypes()) {
     * rc.addAll(identitySession.getRoleManager().findRoles(g, rt)); } } catch
     * (FeatureNotSupportedException ex) { log.error(ex); }
     *
     * gb.setRoleList(rc);
     *
     * gb.setChildren(getSubGroupCol(g)); Collection<Group> plist =
     * identitySession.getRelationshipManager().findAssociatedGroups(g, GROUP,
     * true, true); gb.setParentList(plist);
     *
     * groups.add(gb); }
     *
     * identitySession.getTransaction().commit(); identitySession.close(); }
     * catch (IdentityConfigurationException ex) { log.error(ex); } catch
     * (IdentityException ex) { log.error(ex); } return groups; }
     

    public Collection<String> getAllGroupTypes() {
        Collection<String> groupList = new ArrayList<String>();
        try {

            identitySession.beginTransaction();
            groupList = identitySession.getPersistenceManager().findGroupType((IdentitySearchCriteria) null);
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }
        return groupList;
    }
*/
    public void changeUserPassword(String userId, String p1, String p2) throws IdentityException {
        identitySession.beginTransaction();
        AttributesManager attManager = identitySession.getAttributesManager();
        User user = identitySession.getPersistenceManager().findUser(userId);

        if (!p1.equals(p2)) {
            throw new IdentityException("Passwords don't match!");
        }

        attManager.updateCredential(user, new PasswordCredential(p1));

        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public User createUser(String username) throws IdentityException {
        identitySession.beginTransaction();
        User user = identitySession.getPersistenceManager().createUser(username);
        identitySession.getTransaction().commit();
        identitySession.close();

        return user;
    }

    public Group createGroup(String groupname, String gType) throws IdentityException {
        identitySession.beginTransaction();
        Group group = identitySession.getPersistenceManager().createGroup(groupname, gType);
        identitySession.getTransaction().commit();
        identitySession.close();

        return group;
    }

    @SuppressWarnings("ucd")
    public Group createGroup(String groupname) throws IdentityException {
        return createGroup(groupname, GROUP);
    }

    public void deleteUser(String username) throws IdentityException {
        identitySession.beginTransaction();
        identitySession.getPersistenceManager().removeUser(username, true);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public void deleteGroup(String groupName, String gType) throws IdentityException {
        log.debug("deleting group");
        identitySession.beginTransaction();
        Group group = identitySession.getPersistenceManager().findGroup(groupName, gType);
        identitySession.getPersistenceManager().removeGroup(group, true);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    @SuppressWarnings("ucd")
    public void deleteGroup(String groupName) throws IdentityException {
        deleteGroup(groupName, GROUP);
    }

    public void associateGroup(String pId, String pType, String gId, String gType) throws IdentityException {
        identitySession.beginTransaction();
        
        Group parent = identitySession.getPersistenceManager().findGroup(pId, pType);
        Group child = identitySession.getPersistenceManager().findGroup(gId, gType);
        
        if (!identitySession.getRelationshipManager().isAssociated(parent, child)) {
            identitySession.getRelationshipManager().associateGroups(parent, child);
        }
        
        identitySession.getTransaction().commit();
        identitySession.close();
    }
    
    public void deassociateGroup(String pId, String pType, String gId, String gType) throws IdentityException {
        log.trace("deasssss: "+pId+", "+pType+", "+gId+", "+gType);
        
        identitySession.beginTransaction();

        Group parent = identitySession.getPersistenceManager().findGroup(pId, pType);
        Group child = identitySession.getPersistenceManager().findGroup(gId, gType);
       
        identitySession.getRelationshipManager().disassociateGroups(parent, Arrays.asList(child));

        identitySession.getTransaction().commit();
        identitySession.close();
        
        log.trace("deasssss end: "+pId+", "+pType+", "+gId+", "+gType);
    }

    @SuppressWarnings("ucd")
    public void associateUser(String userId, String groupId) throws IdentityException {
        associateUser(userId, groupId, GROUP);
    }
    
    public void associateUser(String userId, String groupId, String gType) throws IdentityException {
        identitySession.beginTransaction();
        User user = identitySession.getPersistenceManager().findUser(userId);
        Group group = identitySession.getPersistenceManager().findGroup(groupId, gType);
        if (!identitySession.getRelationshipManager().isAssociated(group, user)) {
            identitySession.getRelationshipManager().associateUser(group, user);
        }
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    @SuppressWarnings("ucd")
    public void deassociateUser(String userId, String groupId) throws IdentityException {
        deassociateUser(userId, groupId, GROUP);
    }

    public void deassociateUser(String userId, String groupId, String gType) throws IdentityException {
        log.trace("deas: "+userId+", "+groupId+", "+gType);  
        identitySession.beginTransaction();

        User user = identitySession.getPersistenceManager().findUser(userId);
        Group group = identitySession.getPersistenceManager().findGroup(groupId, gType);

        identitySession.getRelationshipManager().disassociateUsers(Arrays.asList(group), Arrays.asList(user));
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public void createAttribute(String userId, String attName, String attVal) throws IdentityException {
        identitySession.beginTransaction();
        AttributesManager attManager = identitySession.getAttributesManager();
        User user = identitySession.getPersistenceManager().findUser(userId);
        attManager.addAttribute(user, attName, attVal);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public void editAttributeValue(String userId, String attName, String attVal) throws IdentityException {
        identitySession.beginTransaction();
        AttributesManager attManager = identitySession.getAttributesManager();
        User user = identitySession.getPersistenceManager().findUser(userId);
        Attribute[] attribute = new Attribute[]{
            new SimpleAttribute(attName, attVal),};
        attManager.updateAttributes(user, attribute);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public void editAttribute(String userId, String attName, String attVal, String attNameNew, String attValNew) throws IdentityException {
        identitySession.beginTransaction();
        AttributesManager attManager = identitySession.getAttributesManager();
        User user = identitySession.getPersistenceManager().findUser(userId);

        if ("".equals(attNameNew)) {
            throw new IdentityException("New attribute name must be set");
        }

        Attribute[] attribute = new Attribute[]{new SimpleAttribute(attNameNew, attValNew),};
        String[] atts = {attName};

        if (attName.equals(attNameNew)) {
            attManager.updateAttributes(user, attribute);
        } else {
            attManager.removeAttributes(userId, atts);
            attManager.addAttribute(user, attNameNew, attValNew);
        }

        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public void deleteAttribute(String userId, String attName) throws IdentityException {
        identitySession.beginTransaction();
        AttributesManager attManager = identitySession.getAttributesManager();
        User user = identitySession.getPersistenceManager().findUser(userId);
        String[] atts = {attName};
        attManager.removeAttributes(user, atts);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public RoleType createRoletype(String name) throws IdentityException, FeatureNotSupportedException {
        identitySession.beginTransaction();
        RoleManager roleManager = identitySession.getRoleManager();
        RoleType roletype = roleManager.createRoleType(name);
        identitySession.getTransaction().commit();
        identitySession.close();
        return roletype;
    }

    public void deleteRoletype(String name) throws IdentityException, FeatureNotSupportedException {
        identitySession.beginTransaction();
        RoleManager roleManager = identitySession.getRoleManager();
        roleManager.removeRoleType(name);
        identitySession.getTransaction().commit();
    }

    public void associateRole(String roletype, String userId, String groupName) throws IdentityException, FeatureNotSupportedException {
        identitySession.beginTransaction();
        RoleManager roleManager = identitySession.getRoleManager();

        RoleType rt = identitySession.getRoleManager().getRoleType(roletype);
        User u = identitySession.getPersistenceManager().findUser(userId);
        Group g = identitySession.getPersistenceManager().findGroup(groupName, GROUP);
        roleManager.createRole(rt, u, g);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public void deassociateRole(String roletype, String userId, String groupName) throws IdentityException, FeatureNotSupportedException {
        identitySession.beginTransaction();
        RoleManager roleManager = identitySession.getRoleManager();
        RoleType rt = identitySession.getRoleManager().getRoleType(roletype);
        User u = identitySession.getPersistenceManager().findUser(userId);
        Group g = identitySession.getPersistenceManager().findGroup(groupName, GROUP);
        roleManager.removeRole(rt, u, g);
        identitySession.getTransaction().commit();
        identitySession.close();
    }

    public Collection<RoleType> getRoletypesByRange(int offset, int numberOfRows, String query) {
        Collection<RoleType> roletypeList = new ArrayList<RoleType>();
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            IdentitySearchCriteria isc = new IdentitySearchCriteriaImpl().nameFilter("*" + query + "*").page(offset, numberOfRows);
            roletypeList = roleManager.findRoleTypes(isc);
        } catch (UnsupportedCriterium ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        } catch (FeatureNotSupportedException ex) {
            log.error(ex);
        }

        return roletypeList;
    }
    /*
     * public Collection<RoleType> getAllRoletypes() { Collection<RoleType>
     * roletypeList = new ArrayList<RoleType>(); try {
     * identitySession.beginTransaction(); RoleManager roleManager =
     * identitySession.getRoleManager(); roletypeList =
     * roleManager.findRoleTypes(); } catch (IdentityException ex) {
     * log.error(ex); } catch (FeatureNotSupportedException ex) { log.error(ex);
     * }
     *
     * return roletypeList; }
     */

    public int getRoleCount() {
        Collection<RoleBean> roleList = new ArrayList<RoleBean>();

        int c = 0;

        try {
            identitySession.beginTransaction();
            Collection<User> users = identitySession.getPersistenceManager().findUser((IdentitySearchCriteria) null);

            for (User user : users) {
                for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                    Collection<Role> rList = identitySession.getRoleManager().findRoles(user, rt);
                    if (rList != null) {
                        for (Role r : rList) {
                            RoleBean rb = new RoleBean(r.getUser().getId(), r.getGroup().getName(), r.getRoleType().getName(), r.getGroup().getGroupType());
                            c++;
                            roleList.add(rb);
                        }
                    }
                }
            }

            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (FeatureNotSupportedException ex) {
            java.util.logging.Logger.getLogger(IdmProcessor.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return c;
    }

    public int getRoletypeCount() {
        int count = 0;
        try {
            identitySession.beginTransaction();
            RoleManager roleManager = identitySession.getRoleManager();
            count = roleManager.findRoleTypes().size();
        } catch (IdentityException ex) {
            log.error(ex);
        } catch (FeatureNotSupportedException ex) {
            log.error(ex);
        }

        return count;
    }
    /*
     * public Collection<Role> getAllRoles(Group grp) { Collection<Role>
     * roleList = new ArrayList<Role>(); try {
     * identitySession.beginTransaction(); RoleManager roleManager =
     * identitySession.getRoleManager(); roleList = roleManager.findRoles(GROUP,
     * GROUP); } catch (IdentityException ex) { log.error(ex); } catch
     * (FeatureNotSupportedException ex) { log.error(ex); }
     *
     * return roleList; } /* public Collection<? extends RoleBean> getAllRoles()
     * throws FeatureNotSupportedException { Collection<RoleBean> roleList = new
     * ArrayList<RoleBean>();
     *
     * try { identitySession.beginTransaction(); Collection<User> users =
     * identitySession.getPersistenceManager().findUser((IdentitySearchCriteria)
     * null);
     *
     * for (User user : users) { for (RoleType rt :
     * identitySession.getRoleManager().findRoleTypes()) { Collection<Role>
     * rList = identitySession.getRoleManager().findRoles(user, rt); if (rList
     * != null) { for (Role r : rList) { roleList.add(new
     * RoleBean(r.getUser().getId(), r.getGroup().getName(),
     * r.getRoleType().getName(), r.getGroup().getGroupType())); } } } }
     * identitySession.getTransaction().commit(); identitySession.close(); }
     * catch (IdentityConfigurationException ex) { log.error(ex); } catch
     * (IdentityException ex) { log.error(ex); }
     *
     * return roleList; }
     */

    public Collection<? extends RoleBean> getRolesByRange(int offset, int numberOfRows, String query) throws FeatureNotSupportedException {
        Collection<RoleBean> roleList = new ArrayList<RoleBean>();

        int c = 0;

        try {
            identitySession.beginTransaction();
            Collection<User> users = identitySession.getPersistenceManager().findUser((IdentitySearchCriteria) null);

            for (User user : users) {
                for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                    Collection<Role> rList = identitySession.getRoleManager().findRoles(user, rt);
                    if (rList != null) {
                        for (Role r : rList) {
                            RoleBean rb = new RoleBean(r.getUser().getId(), r.getGroup().getName(), r.getRoleType().getName(), r.getGroup().getGroupType());
                            c++;
                            log.trace("c: " + c + ", offset: " + offset + ", num: " + numberOfRows);
                            if ((c > offset) && (c <= offset + numberOfRows)) {
                                roleList.add(rb);
                            }
                        }
                    }
                }
            }
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return roleList;
    }

    public Collection<? extends RoleBean> getRolesForUser(String rUser) throws FeatureNotSupportedException {
        Collection<RoleBean> roleList = new ArrayList<RoleBean>();

        try {
            identitySession.beginTransaction();
            User user = identitySession.getPersistenceManager().findUser(rUser);

            for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                Collection<Role> rList = identitySession.getRoleManager().findRoles(user, rt);
                if (rList != null) {
                    for (Role r : rList) {
                        roleList.add(new RoleBean(r.getUser().getId(), r.getGroup().getName(), r.getRoleType().getName(), r.getGroup().getGroupType()));
                    }
                }
            }
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return roleList;
    }

    public Collection<? extends RoleBean> getRolesForGroup(String rGroup, String gType) throws FeatureNotSupportedException {
        Collection<RoleBean> roleList = new ArrayList<RoleBean>();

        try {
            identitySession.beginTransaction();
            Group group = identitySession.getPersistenceManager().findGroup(rGroup, gType);

            for (RoleType rt : identitySession.getRoleManager().findRoleTypes()) {
                Collection<Role> rList = identitySession.getRoleManager().findRoles(group, rt);
                if (rList != null) {
                    for (Role r : rList) {
                        roleList.add(new RoleBean(r.getUser().getId(), r.getGroup().getName(), r.getRoleType().getName(), r.getGroup().getGroupType()));
                    }
                }
            }
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return roleList;
    }

    public Collection<? extends RoleBean> getRolesForRoletype(String rRoletype) throws FeatureNotSupportedException {
        Collection<RoleBean> roleList = new ArrayList<RoleBean>();

        try {
            identitySession.beginTransaction();
            Collection<User> userCol = identitySession.getPersistenceManager().findUser((IdentitySearchCriteria) null);
            RoleType rt = identitySession.getRoleManager().getRoleType(rRoletype);

            for (User u : userCol) {
                Collection<Role> rList = identitySession.getRoleManager().findRoles(u, rt);
                if (rList != null) {
                    for (Role r : rList) {
                        roleList.add(new RoleBean(r.getUser().getId(), r.getGroup().getName(), r.getRoleType().getName(), r.getGroup().getGroupType()));
                    }
                }
            }
            identitySession.getTransaction().commit();
            identitySession.close();
        } catch (IdentityConfigurationException ex) {
            log.error(ex);
        } catch (IdentityException ex) {
            log.error(ex);
        }

        return roleList;
    }
}
