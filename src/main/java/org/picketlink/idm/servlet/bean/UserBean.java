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
import org.picketlink.idm.api.User;

/**
 *
 * @author vrockai
 */
public class UserBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1026519149350826392L;
	private User user;
    private Collection<Group> associatedGroups;
    private String userId;
    private byte[] photo_image;

    private boolean photo = false;

    public boolean isPhoto() {
        return photo_image.length > 1;
    }
        
    public byte[] getPhoto() {
        return photo_image;
    }

    public void setPhoto(byte[] photo) {
        this.photo_image = photo;
    }
    private Collection<AttributeBean> attributes;
    private String fname;
    private String lname;
    private String email;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public UserBean(User user) {
        this.userId = user.getId();
    }

    public Collection<Group> getAssociatedGroups() {
        return associatedGroups;
    }

    public void setAssociatedGroups(Collection<Group> associatedGroups) {
        this.associatedGroups = associatedGroups;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Collection<AttributeBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<AttributeBean> attributes) {
        this.attributes = attributes;
    }

    public String getHash() {
        return userId.replace(" ", "_");
    }

}
