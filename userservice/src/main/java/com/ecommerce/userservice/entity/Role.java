package com.ecommerce.userservice.entity;

import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.Set;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int role_id;
    @Column(nullable = false)
    private String role;
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private Set<User_role> users=new HashSet<>();

    public Role() {
    }

    public Role(String role) {
        this.role = role;
    }

    public int getRole_id() {
        return role_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<User_role> getUsers() {
        return users;
    }

    public void addRelUser(User_role newUser){
        users.add(newUser);
    }

    public void removeRelUser(User_role userR){
        users.remove(userR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return role_id == role.role_id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(role_id);
    }
}
