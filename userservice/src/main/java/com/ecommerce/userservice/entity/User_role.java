package com.ecommerce.userservice.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;

@Entity
public class User_role {
    @EmbeddedId
    User_roleskey id;

    @OneToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    User user; 

    @ManyToOne
    @MapsId("role_id")
    @JoinColumn(name = "role_id")
    Role role;

    public User_role() {
    }

    public User_role(User user, Role role) {
        this.user = user;
        this.role = role;
        this.id = new User_roleskey(user.getUser_id(), role.getRole_id());
    }

    public User_role(User_roleskey key, User user, Role role) {
        this.user = user;
        this.role = role;
        this.id = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User_role that = (User_role) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
