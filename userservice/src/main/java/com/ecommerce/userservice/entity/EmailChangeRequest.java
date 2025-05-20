package com.ecommerce.userservice.entity;

public class EmailChangeRequest {
    public String email;
    public String password;

    public EmailChangeRequest(String email,String password){
        this.email=email;
        this.password=password;
    }
}