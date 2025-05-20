package com.ecommerce.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long user_id;
    @Column(nullable = false, unique=true)
    private String username;
    @Column(nullable = false)
    private String password;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private User_role role;
    @Column(name = "last_password_reset")
    private LocalDateTime lastPasswordReset;
    @Column(name = "otp")
    private String mfaOtp;
    @Column(name = "otp_expiry")
    private LocalDateTime mfaOtpExpiry;
    @Column(nullable = false, unique=true)
    private String email;
    @Column(columnDefinition = "boolean default false",name = "account_validated")
    private boolean acvalidated;
    private String old_email;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email=email;
        lastPasswordReset=LocalDateTime.now();
    }

    public boolean getAcvalidated(){
        return acvalidated;
    }

    public void setAcvalidated(boolean acvalidated){
        this.acvalidated=acvalidated;
    }

    public long getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public String getOldEmail(){
        return old_email;
    }

    public void setOldEmail(String old_email){
        this.old_email=old_email;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getLastPasswordReset(){
        return lastPasswordReset;
    }

    public void setLastPasswordReset(){
        lastPasswordReset=LocalDateTime.now();
    }

    public void setPassword(String password) {
        this.password = password;
        lastPasswordReset=LocalDateTime.now();
    }

    public Role getRole(){
        return role.getRole();
    }

    public boolean setRole(Role role) {
        User_role newRole=new User_role(this,role);
        this.role=newRole;
        role.addRelUser(newRole);   
        System.out.println(this.role.getRole().getRole()); 
        return true;
    }

    public boolean removeRole(){
        role.getRole().removeRelUser(role);
        this.role=null;
        return true;
    }

    public String getMfaOTP(){
        return mfaOtp;
    }

    public void setMfaOtp(String mfaOtp){
        this.mfaOtp=mfaOtp;
    }

    public LocalDateTime getMfaOtpExpiry(){
        return mfaOtpExpiry;
    }

    public void setMfaOtpExpiry(LocalDateTime mfaOtpExpiry){
        this.mfaOtpExpiry=mfaOtpExpiry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return user_id == user.user_id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(user_id);
    }
}
