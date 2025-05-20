package com.ecommerce.userservice.entity;

public class SimpleUser {
    private String username;
    private String password;
    
    public SimpleUser(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    
}
