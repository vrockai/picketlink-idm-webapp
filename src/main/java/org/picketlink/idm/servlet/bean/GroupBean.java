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



package org.picketlink.idm.servlet.bean;

import java.io.Serializable;
import java.util.Collection;

import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.Role;

/**
 *
 * @author vrockai
 */
public class GroupBean  implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6417353401202267507L;
	private Group group;
    private Collection<GroupBean> children;
    private Collection<Group> parentList;
    
    private String type = "^notset^";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<Group> getParentList() {
        return parentList;
    }

    public void setParentList(Collection<Group> parentList) {
        this.parentList = parentList;
    }

    public Collection<GroupBean> getChildren() {
        return children;
    }

    public void setChildren(Collection<GroupBean> children) {
        this.children = children;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(Collection<Role> roleList) {
        this.roleList = roleList;
    }
    private String name;
    private Collection<Role> roleList;

    public GroupBean(Group group){
        this.name=group.getName();
        this.type=group.getGroupType();
    }
    
    public String getHash(){
        return name.replace(" ", "_");
    }
    
    public boolean isParent(){
        return children == null ? false : !children.isEmpty();
    }
    
    public int getChildrenCount(){
        return children == null ? 0 : children.size();
    }
    
    public String toString(){
        return "("+this.name+": "+this.type+")";
    }
}
