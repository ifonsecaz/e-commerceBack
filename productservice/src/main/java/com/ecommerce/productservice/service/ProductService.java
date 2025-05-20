package com.ecommerce.productservice.service;

import java.util.List;
import java.util.Optional;

import com.ecommerce.productservice.entity.*;
import com.ecommerce.productservice.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService{
    @Autowired 
    private ProductRepository productRepository;
    
    //Save
    public Product saveProduct(Product product){
        return productRepository.save(product);
    }

    //Read  
    public List<Product> fetchProductList(){
        return productRepository.findAll();
    }

    //Read 1
    public Product fetchProductByID(Long productID){
        Optional<Product> res= productRepository.findById(productID);
        if(res.isPresent()){
            return res.get();
        }
        return null;
    }

    public List<Product> fetchProductByName(String name){
        return productRepository.findByProductNameContaining(name);
    }

    public List<Product> fetchProductByCategory(String category){
        return productRepository.findByCategory(category);
    }

    //Delete
    public boolean deleteProduct(Long productID){
        boolean res=false;
        if(productRepository.findById(productID).isPresent()){
            res=true;
            productRepository.deleteById(productID);
        }
        return res;
    }

    //Update
    public Product updateProduct(Product product, Long productID){
        if(productRepository.findById(productID).isPresent()){
            Product aux=productRepository.findById(productID).get();

            if(!aux.getProduct_name().equals(product.getProduct_name()))
                aux.setProduct_name(product.getProduct_name());
            if(!aux.getDescription().equals(product.getDescription()))
                aux.setDescription(product.getDescription());
            if(!aux.getCategory().equals(product.getCategory()))
                aux.setCategory(product.getCategory());
            if(aux.getPrice()!=product.getPrice())
                aux.setPrice(product.getPrice());
            if(aux.getStock_quantity()!=product.getStock_quantity())
                aux.setStock_quantity(product.getStock_quantity());
            return productRepository.save(aux);

        }
        return null;
    }

    public Product updateRefillStock(Long productID, int units){
        if(productRepository.findById(productID).isPresent()){
            Product aux=productRepository.findById(productID).get();
            
            aux.setStock_quantity(aux.getStock_quantity()+units);
                        

            return productRepository.save(aux);
        }
        return null;
    }

    public ResponseEntity<?> updateRemoveStock(Long productID, int units){
        if(productRepository.findById(productID).isPresent()){
            Product aux=productRepository.findById(productID).get();
            
            if(aux.getStock_quantity()>=units){
                aux.setStock_quantity(aux.getStock_quantity()-units);
                return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(aux));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is not enough units");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }

    public Product updatePrice(Long productID, double price){
        if(productRepository.findById(productID).isPresent()){
            Product aux=productRepository.findById(productID).get();
            
            aux.setPrice(price);
            
            return productRepository.save(aux);
        }
        return null;
    }

}