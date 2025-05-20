package com.ecommerce.userservice.entity;

import java.time.LocalDate;

public class OrdersDTO {
    private long order_id;
    private long user_id;
    private LocalDate order_date;
    private double total_amount;
    private String order_status;

    public OrdersDTO(){

    }

    public OrdersDTO(long user_id, LocalDate order_date, double total_amount, String order_status){
        this.user_id=user_id;
        this.order_date=order_date;
        this.total_amount=total_amount;
        this.order_status=order_status;
    }

    public long getOrder_id(){
        return order_id;
    }

    public long getUser_id(){
        return user_id;
    }

    public LocalDate getOrder_date(){
        return order_date;
    }

    public double getTotal_amount(){
        return total_amount;
    }


    public String getOrder_status(){
        return order_status;
    }

}
