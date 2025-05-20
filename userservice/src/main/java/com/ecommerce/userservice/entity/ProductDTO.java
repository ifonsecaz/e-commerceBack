package com.ecommerce.userservice.entity;

public class ProductDTO {
    private long product_id;
    private String product_name;
    private String category;
    private String description;
    private double price;
    private int stock_quantity;

    public ProductDTO(long product_id, String product_name, String category, String description, double price, int stock_quantity){
        this.product_id=product_id;
        this.product_name=product_name;
        this.category=category;
        this.description=description;
        this.price=price;
        this.stock_quantity=stock_quantity;
    }

    public long getProduct_id(){
        return product_id;
    }

    public String getProduct_name(){
        return product_name;
    }

    public String getCategory(){
        return category;
    }

    public String getDescription(){
        return description;
    }

    public double getPrice(){
        return price;
    } 

    public int getStock_quantity(){
        return stock_quantity;
    }

}