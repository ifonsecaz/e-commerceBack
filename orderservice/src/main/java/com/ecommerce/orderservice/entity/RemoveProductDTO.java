package com.ecommerce.orderservice.entity;

public class RemoveProductDTO {
    public long user_id;
    public long product_id;

    public RemoveProductDTO(){
        
    }

    public RemoveProductDTO(long user_id, long product_id){
        this.user_id=user_id;
        this.product_id=product_id;
    }
}

