package com.ecommerce.paymentservice.controller;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/payments")
public class PaymentController {
     @Autowired private PaymentService paymentService;
    
    @PostMapping("/pay")
    public ResponseEntity<?> savePayment(
        @Valid @RequestBody Payment payment)
    {
        Payment res=paymentService.savePayment(payment);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Verify the data");
        }
    }

    @GetMapping("/paymentlist")
    public List<Payment> fetchPaymentList()
    {
        return paymentService.fetchPaymentsList();
    }

    @GetMapping("/payment/{id}")
    public ResponseEntity<?> fetchPaymentById(@PathVariable("id") Long paymentId)
    {
        Payment res= paymentService.fetchPayment(paymentId);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }

    @GetMapping("/paymentlist/user/{id}")
    public List<Payment> fetchPaymentListUser(@PathVariable("id") Long user_id)
    {
        return paymentService.fetchUsersPaymentsList(user_id);
    }

    @GetMapping("/payment/order/{id}")
    public ResponseEntity<?> fetchPaymentByOrder(@PathVariable("id") Long orderId)
    {
        Payment res= paymentService.fetchPaymentByOrder(orderId);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectPayment(
        @PathVariable("id") Long payment_id)
    {
        Payment res=paymentService.rejectPayment(payment_id);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Verify the data");
        }
    }

}
