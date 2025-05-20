package com.ecommerce.productservice.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Product {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long product_id;
    @Column(nullable = false, unique = true)
    private String product_name;
    private String category;
    private String description;
    @Column(nullable = false)
    private double price;
    private int stock_quantity;

    public Product(){
    }

    public Product(String product_name, String category, String description, double price, int stock_quantity){
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

    public void setProduct_name(String product_name){
        this.product_name=product_name;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category=category;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public double getPrice(){
        return price;
    } 

    public void setPrice(double price){
        this.price=price;
    }

    public int getStock_quantity(){
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity){
        this.stock_quantity=stock_quantity;
    }
}