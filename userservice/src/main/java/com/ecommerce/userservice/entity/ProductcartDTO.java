package com.ecommerce.userservice.entity;

public class ProductcartDTO {
    public long user_id;
    public long product_id;
    public int quantity;
    public double unit_price;

    public ProductcartDTO(){
        
    }

    public ProductcartDTO(long user_id, long product_id, int quantity, double unit_price){
        this.user_id=user_id;
        this.product_id=product_id;
        this.quantity=quantity;
        this.unit_price=unit_price;
    }

    public ProductcartDTO(long product_id, int quantity){
        this.product_id=product_id;
        this.quantity=quantity;
    }
}
