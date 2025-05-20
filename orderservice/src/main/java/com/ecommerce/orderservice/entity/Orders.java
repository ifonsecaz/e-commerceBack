package com.ecommerce.orderservice.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Iterator;
import org.codehaus.jackson.annotate.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long order_id;
    @Column(name="user_id", nullable = false)
    private long userId;
    private LocalDate order_date;
    private double total_amount;
    private String order_status;
    @OneToMany(mappedBy="order",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Orderdetails> items=new HashSet<>();

    public Orders(){

    }

    public Orders(long userId, LocalDate order_date, double total_amount, String order_status){
        this.userId=userId;
        this.order_date=order_date;
        this.total_amount=total_amount;
        this.order_status=order_status;
    }

    public long getOrder_id(){
        return order_id;
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

    public double addProduct(double total){
        total_amount+=total;
        return total_amount;
    }

    public double removeProductPrice(double total){
        total_amount-=total;
        return total_amount;
    }

    public String getOrder_status(){
        return order_status;
    }

    public void setOrder_status(String order_status){
        this.order_status=order_status;
    }

    public Set<Orderdetails> getOrder_details() {
        return items;
    }

    public void addOrder_details(Orderdetails newItem) {
        items.add(newItem);
    }

    public void removeOrder_details(Orderdetails newItem) {
        items.remove(newItem);
    }

    public void removeProduct(long product_id) {
        Iterator<Orderdetails> it=items.iterator();
        while(it.hasNext()){
            Orderdetails aux=it.next();
            if(aux.getProduct_id()==product_id){
                aux.removeOrders();
                it.remove();
            }
        }  
    }

    public void removeOrder(){
        Iterator<Orderdetails> it=items.iterator();
        while(it.hasNext()){
            Orderdetails aux=it.next();
            aux.removeOrders();
            it.remove();
        }        
    }
}
