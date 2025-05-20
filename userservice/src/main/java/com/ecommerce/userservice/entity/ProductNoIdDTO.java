package com.ecommerce.userservice.entity;

public class ProductNoIdDTO {
    private String product_name;
    private String category;
    private String description;
    private double price;
    private int stock_quantity;

    public ProductNoIdDTO(String product_name, String category, String description, double price, int stock_quantity){
        this.product_name=product_name;
        this.category=category;
        this.description=description;
        this.price=price;
        this.stock_quantity=stock_quantity;
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