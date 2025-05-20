package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.entity.Orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;



@Repository
public interface OrdersRepository extends JpaRepository<Orders,Long>{
    @Query(value="SELECT order_id FROM orders where user_id=?1 and order_status='Not completed' Order by order_id desc limit 1", nativeQuery = true)
    Optional<Long> findLastOrder(long user_id);

    List<Orders> findByUserId(long user_id);
}
