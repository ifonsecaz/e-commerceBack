package com.ecommerce.userservice.entity;

public class PasswordResetRequest {
    public String password;
    public String new_password;
    public String confirm_password;

    public PasswordResetRequest(String password,String new_password,String confirm_password){
        this.password=password;
        this.new_password=new_password;
        this.confirm_password=confirm_password;
    }
}