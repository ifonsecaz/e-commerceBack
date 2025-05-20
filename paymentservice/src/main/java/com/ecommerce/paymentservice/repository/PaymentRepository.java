package com.ecommerce.paymentservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.paymentservice.entity.Payment;
import java.util.List;


@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long>{
    Optional<Payment> findByOrderId(long orderId);

    List<Payment> findByUserId(long userId);
}
