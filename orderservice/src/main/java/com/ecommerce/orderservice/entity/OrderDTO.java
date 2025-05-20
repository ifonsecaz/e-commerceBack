package com.ecommerce.orderservice.entity;
import java.time.LocalDate;

public class OrderDTO {
    private long order_id;
    private long userId;
    private LocalDate order_date;
    private double total_amount;
    private String order_status;

    public OrderDTO(){

    }

    public OrderDTO(long order_id, long userId, LocalDate order_date, double total_amount, String order_status){
        this.order_id=order_id;
        this.userId=userId;
        this.order_date=order_date;
        this.total_amount=total_amount;
        this.order_status=order_status;
    }

    public long getOrder_id(){
        return order_id;
    }

    public void setOrder_id(long order_id){
        this.order_id=order_id;
    }

    public long getUser_id(){
        return userId;
    }

    public void setUser_id(long userId){
        this.userId=userId;
    }

    public LocalDate getOrder_date(){
        return order_date;
    }

    public void setOrder_date(LocalDate order_date){
        this.order_date=order_date;
    }

    public double getTotal_amount(){
        return total_amount;
    }

    public void setTotal_amount(double total_amount){
        this.total_amount=total_amount;
    }

    public String getOrder_status(){
        return order_status;
    }

    public void setOrder_status(String order_status){
        this.order_status=order_status;
    }
}
