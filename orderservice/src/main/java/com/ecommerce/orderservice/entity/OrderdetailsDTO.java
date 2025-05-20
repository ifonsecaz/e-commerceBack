package com.ecommerce.orderservice.entity;

public class OrderdetailsDTO {
    private long order_detail_id;
    private long product_id;
    private int quantity;
    private double unit_price;

    public OrderdetailsDTO(){

    }

    public OrderdetailsDTO(long order_detail_id,long product_id, int quantity, double unit_price){
        this.order_detail_id=order_detail_id;
        this.product_id=product_id;
        this.quantity=quantity;
        this.unit_price=unit_price;
    }

    public long getOrder_detail_id(){
        return order_detail_id;
    }

    public void setOrder_detail_id(long order_detail_id){
        this.order_detail_id=order_detail_id;
    }

    public long getProduct_id(){
        return product_id;
    }

    public void setProduct_id(long product_id){
        this.product_id=product_id;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity){
        this.quantity=quantity;
    }

    public double getUnit_price(){
        return unit_price;
    }

    public void setUnit_price(double unit_price){
        this.unit_price=unit_price;
    }
}
