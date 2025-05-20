package com.ecommerce.paymentservice.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long payment_id;
    @Column(name="order_id", nullable = false)
    private long orderId;
    @Column(name="user_id", nullable = false)
    private long userId;
    private LocalDate payment_date;
    private double amount;
    private String payment_method;
    private String payment_status;

    //only one pay  
    public Payment(){
    }

    public Payment(long orderId, long userID, LocalDate paymenDate, double amount, String payment_method, String payment_status){
        this.orderId=orderId;
        this.userId=userID;
        this.payment_date=paymenDate;
        this.amount=amount;
        this.payment_method=payment_method;
        this.payment_status=payment_status;
    }

    public long getPayment_id() {
        return payment_id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userID) {
        this.userId = userID;
    }

    public LocalDate getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(LocalDate payment_date) {
        this.payment_date = payment_date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_status(){
        return payment_status;
    }

    public void setPayment_status(String payment_status){
        this.payment_status=payment_status;
    }
}
