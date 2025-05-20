package com.ecommerce.productservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.productservice.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>{
    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.product_name LIKE %:product_name%")
    List<Product> findByProductNameContaining(@Param("product_name") String product_name);

    //Search by price range, or in stock
}