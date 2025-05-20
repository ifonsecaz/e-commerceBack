package com.ecommerce.userservice.entity;

public class OrderdetailsDTO {
    private long order_detail_id;
    private long product_id;
    private int quantity;
    private double unit_price;

    public OrderdetailsDTO(){

    }

    public OrderdetailsDTO(long product_id, int quantity, double unit_price){
        this.product_id=product_id;
        this.quantity=quantity;
        this.unit_price=unit_price;
    }

    public long getOrder_detail_id(){
        return order_detail_id;
    }

    public long getProduct_id(){
        return product_id;
    }

   
    public int getQuantity(){
        return quantity;
    }

    public double getUnit_price(){
        return unit_price;
    }
}
