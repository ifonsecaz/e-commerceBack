package com.ecommerce.orderservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
//import com.fasterxml.jackson.annotation.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnore;
import jakarta.persistence.Column;

@Entity
public class Orderdetails {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long order_detail_id;
    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    @JsonIgnore
    private Orders order;
    //private long order_id;
    private long product_id;
    private int quantity;
    private double unit_price;

    public Orderdetails(){

    }

    public Orderdetails(Orders order, long product_id, int quantity, double unit_price){
        this.order=order;
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

    public Orders getOrders(){
        return order;
    }

    public void setOrders(Orders order) {
        this.order=order;
    }

    public void removeOrders() {
        this.order=null;
    }
}
