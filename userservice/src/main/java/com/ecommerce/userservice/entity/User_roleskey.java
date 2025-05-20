package com.ecommerce.userservice.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class User_roleskey implements Serializable{
    private Long user_id;
    private int role_id;

    //composite key
    public User_roleskey(){

    }

    public User_roleskey(Long user_id, int role_id){
        this.user_id=user_id;
        this.role_id=role_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User_roleskey)) return false;
        User_roleskey that = (User_roleskey) o;
        return Objects.equals(user_id, that.user_id) &&
               Objects.equals(role_id, that.role_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, role_id);
    }
}