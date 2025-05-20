package com.ecommerce.paymentservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.repository.PaymentRepository;

@Service
public class PaymentService {
    @Autowired 
    private PaymentRepository paymentRepository;
    
    //pay order change status
    public Payment savePayment(Payment payment){
        return paymentRepository.save(payment);
    }

    //get payments admin
    public List<Payment> fetchPaymentsList(){
        return paymentRepository.findAll();
    }

    //get payment by id
    public Payment fetchPayment(long payment_id){
        if(paymentRepository.findById(payment_id).isPresent())
            return paymentRepository.findById(payment_id).get();
        return null;
    }

    //get payments user
    public List<Payment> fetchUsersPaymentsList(long user_id){
        return paymentRepository.findByUserId(user_id);
    }  

    //get payment by order
    public Payment fetchPaymentByOrder(long order_id){
        if(paymentRepository.findByOrderId(order_id).isPresent())
            return paymentRepository.findByOrderId(order_id).get();
        return null;
    }
    
    //cancel payment revert status
    public Payment rejectPayment(long payment_id){
        if(paymentRepository.findById(payment_id).isPresent()){
            Payment aux=paymentRepository.findByOrderId(payment_id).get();
            aux.setPayment_status("Rejected");
            return aux;
        }
        return null;
    }

}
