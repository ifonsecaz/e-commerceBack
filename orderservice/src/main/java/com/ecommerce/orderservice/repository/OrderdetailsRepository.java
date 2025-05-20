package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.Orderdetails;
import com.ecommerce.orderservice.entity.Orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderdetailsRepository extends JpaRepository<Orderdetails,Long>{
    List<Orderdetails> findByOrder(Orders orders);
}
