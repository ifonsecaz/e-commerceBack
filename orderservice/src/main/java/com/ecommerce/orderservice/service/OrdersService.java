package com.ecommerce.orderservice.service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import com.ecommerce.orderservice.entity.*;
import com.ecommerce.orderservice.repository.*;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersService{
    @Autowired 
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderdetailsRepository orderdetailsRepository;

    //add product, works like a cart
    @Transactional
    public Orders addProduct(ProductDTO order){
        Optional<Long> auxOrderID=ordersRepository.findLastOrder(order.user_id);
        if(auxOrderID.isPresent()){
            Long order_id=auxOrderID.get();
            //an open order exists, then append the product
            Orders aux=ordersRepository.findById(order_id).get();
            Iterator<Orderdetails> it=aux.getOrder_details().iterator();
            Orderdetails newODetails=null;
            while(it.hasNext()){
                Orderdetails item=it.next();
                if(item.getProduct_id()==order.product_id){
                    newODetails=item;
                }
            } 
            
            if(newODetails==null){
                newODetails=new Orderdetails(aux,order.product_id,order.quantity,order.unit_price);
                aux.addOrder_details(newODetails);
            }
            else{
                newODetails.setQuantity(newODetails.getQuantity()+order.quantity);
            }
            
            orderdetailsRepository.save(newODetails);

            aux.addProduct(order.quantity*order.unit_price);
            return aux;
        }
        else{
            //create new order, status not completed, should only exist one
            Orders newOrder=new Orders(order.user_id,LocalDate.now(),order.quantity*order.unit_price,"Not completed");
            ordersRepository.save(newOrder);
            Orderdetails newODetails=new Orderdetails(newOrder,order.product_id,order.quantity,order.unit_price);
            orderdetailsRepository.save(newODetails);
            newOrder.addOrder_details(newODetails);
            return newOrder;
        }
    }

    //remove a product
    @Transactional
    public Orders removeProduct(long user_id, long product_id){
        
        Orders aux =null;
        Optional<Long> auxOrderID=ordersRepository.findLastOrder(user_id);
        if(auxOrderID.isPresent()){
            Long order_id=auxOrderID.get();
            aux=ordersRepository.findById(order_id).get();
            Set<Orderdetails> listOrd=aux.getOrder_details();
            double price=0;
            for(Orderdetails item:listOrd){
                if(item.getProduct_id()==product_id){
                    price+=item.getQuantity()*item.getUnit_price();
                }
            }
            aux.removeProductPrice(price);
            aux.removeProduct(product_id);
            ordersRepository.save(aux);
        }
        return aux;
    }

    //empty cart
    @Transactional
    public void emptyCart(long user_id){
        Optional<Long> auxOrderID=ordersRepository.findLastOrder(user_id);
        if(auxOrderID.isPresent()){
            Long order_id=auxOrderID.get();
            Orders aux=ordersRepository.findById(order_id).get();
            aux.removeOrder();
            ordersRepository.delete(aux);
        }
    }

    //Cancel order, needs more logic
    @Transactional
    public boolean cancelOrder(long user_id, long order_id){
        boolean res=false;
        if(ordersRepository.findById(order_id).isPresent()){
            Orders aux=ordersRepository.findById(order_id).get();
            if(aux.getUser_id()==user_id){
                aux.removeOrder();
                res=true;
                ordersRepository.delete(aux);
            }
        }
        return res;
    }

    //Cancel any order, only admin
    @Transactional
    public boolean cancelOrderAdmin(long order_id){
        boolean res=false;
        if(ordersRepository.findById(order_id).isPresent()){
            Orders aux=ordersRepository.findById(order_id).get();
            aux.removeOrder();
            res=true;
            ordersRepository.delete(aux);
        }
        return res;
    }

    //complete order, proceed to payout
    public Orders completeOrder(long user_id){
        Orders res=null;
        Optional<Long> auxOrderID=ordersRepository.findLastOrder(user_id);
        if(auxOrderID.isPresent()){
            Long order_id=auxOrderID.get();
            res=ordersRepository.findById(order_id).get();
            res.setOrder_status("Processing");
            res.setOrder_date(LocalDate.now());
            ordersRepository.save(res);
        }
        return res;
    }

    //payout completed
    public Orders completePaymentOrder(long order_id){
        Orders res=null;
        if(ordersRepository.findById(order_id).isPresent()){
            res=ordersRepository.findById(order_id).get();
            if(res.getOrder_status().equals("Processing")){
                res.setOrder_status("To ship");
                ordersRepository.save(res);
            }
        }
        return res;
    }

    //payment rejected
    public Orders rejectedPaymentOrder(long order_id){
        Orders res=null;
        if(ordersRepository.findById(order_id).isPresent()){
            res=ordersRepository.findById(order_id).get();
            if(res.getOrder_status().equals("To ship")){
                res.setOrder_status("Processing");
                ordersRepository.save(res);
            }
        }
        return res;
    }

    //reduce products quantity +1
    @Transactional
    public Orders reduceProduct(long user_id, long product_id){
        Orders aux=null;
        Optional<Long> auxOrderID=ordersRepository.findLastOrder(user_id);
        if(auxOrderID.isPresent()){
            Long order_id=auxOrderID.get();
            aux=ordersRepository.findById(order_id).get();
            Set<Orderdetails> listOrd=aux.getOrder_details();
            for(Orderdetails item:listOrd){
                if(item.getProduct_id()==product_id && item.getQuantity()>1){
                    item.setQuantity(item.getQuantity()-1);
                    aux.removeProductPrice(item.getUnit_price());
                    ordersRepository.save(aux);
                    break;
                }
                else if(item.getProduct_id()==product_id && item.getQuantity()==0){
                    removeProduct(user_id, product_id);
                    break;
                }
            }
        }
        return aux;
    }

    //increase products quantity -1
    @Transactional
    public Orders increaseProduct(long user_id, long product_id){
        Orders aux=null;
        Optional<Long> auxOrderID=ordersRepository.findLastOrder(user_id);
        if(auxOrderID.isPresent()){
            Long order_id=auxOrderID.get();
            aux=ordersRepository.findById(order_id).get();
            Set<Orderdetails> listOrd=aux.getOrder_details();
            for(Orderdetails item:listOrd){
                if(item.getProduct_id()==product_id && item.getQuantity()>1){
                    item.setQuantity(item.getQuantity()+1);
                    aux.addProduct(item.getUnit_price());
                
                    break;
                }
            }
            ordersRepository.save(aux);
        }
        return aux;
    }


    //get orders, only admins
    public List<Orders> fetchOrdersList(){
        return ordersRepository.findAll();
    }

    public Orders fetchOrder(long order_id){
        if(ordersRepository.findById(order_id).isPresent())
            return ordersRepository.findById(order_id).get();
        return null;
    }

    public long viewCart(long user_id){
        Optional<Long> aux=ordersRepository.findLastOrder(user_id);
        if(aux.isPresent())
            return aux.get();
        return 0;
    }

    //get your orders
    public List<Orders> fetchUsersOrdersList(long user_id){
        return ordersRepository.findByUserId(user_id);
    }

    //view 1 order details for admin
    public List<Orderdetails> fetchOrdersDetailListAdmin(long order_id){
        List<Orderdetails> res=null;
        if(ordersRepository.findById(order_id).isPresent()){
            Orders aux=ordersRepository.findById(order_id).get();
            res=orderdetailsRepository.findByOrder(aux);
        }
        return res;
    }

    //view users order details
    public List<Orderdetails> fetchOrdersDetailList(long user_id,long order_id){
        List<Orderdetails> res=null;
        if(ordersRepository.findById(order_id).isPresent()){
            Orders aux=ordersRepository.findById(order_id).get();
            if(aux.getUser_id()==user_id)
                res=orderdetailsRepository.findByOrder(aux);
        }
        return res;
    }

    //Not implemented, view all your orders with details
}
