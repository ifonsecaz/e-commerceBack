package com.ecommerce.productservice.controller;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.ecommerce.productservice.entity.*;
import com.ecommerce.productservice.service.*;


@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired private ProductService productService;
    
    @PostMapping("/add")
    public ResponseEntity<?> saveProduct(
        @Valid @RequestBody Product product)
    {
        Product res=productService.saveProduct(product);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Verify the data");
        }
    }

    @GetMapping("/productlist")
    public List<Product> fetchProductList()
    {
        return productService.fetchProductList();
    }

    @GetMapping("/product/name/{name}")
    public List<Product> fetchProductName(@PathVariable("name") String name)
    {
        return productService.fetchProductByName(name);
    }

    @GetMapping("/product/{id}/units")
    public int fetchProductUnits(@PathVariable("id") long product_id)
    {
        Product aux= productService.fetchProductByID(product_id);
        int res=0;
        if(aux!=null)
            res=aux.getStock_quantity();
        return res;
    }

    @GetMapping("/product/{id}/price")
    public double fetchProductPrice(@PathVariable("id") long product_id)
    {
        Product aux= productService.fetchProductByID(product_id);
        double res=0;
        if(aux!=null)
            res=aux.getPrice();
        return res;
    }

    @GetMapping("/product/category/{category}")
    public List<Product> fetchProductCategory(@PathVariable("category") String category)
    {
        return productService.fetchProductByCategory(category);
    }


    @GetMapping("/product/{id}")
    public ResponseEntity<?> fetchProductById(@PathVariable("id") Long productID)
    {
        Product res= productService.fetchProductByID(productID);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }
 
    @PutMapping("/update/product/{id}")
    public ResponseEntity<?>
    updateProduct(@Valid @RequestBody Product product,
                     @PathVariable("id") Long productId)
    {
        Product res= productService.updateProduct(product, productId);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }
    
    @PutMapping("/update/product/{id}/price/{price}")
    public ResponseEntity<?>
    updateProductPrice(@PathVariable("id") Long productId,
                     @PathVariable("price") double price)
    {
        if(price<1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The product price can't be lower than $1");
        }
        Product res= productService.updatePrice(productId, price);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }

    //call to create update order
    @PutMapping("/update/product/{id}/unitsbuy/{units}")
    public ResponseEntity<?>
    updateProductUnits(@PathVariable("id") Long productId,
                        @PathVariable("units") int units)
    {
        if(units<1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Units buyed can't be negative");
        }
        return productService.updateRemoveStock(productId, units);
    }

    //called when removing object
    @PutMapping("/update/product/{id}/refill/{units}")
    public ResponseEntity<?>
    updateProductRefill(@PathVariable("id") Long productId,
                        @PathVariable("units") int units)
    {
        if(units<1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Units buyed can't be negative");
        }
        Product res= productService.updateRefillStock(productId, units);
        if(res!=null){
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id")
                                       Long productId)
    {
        if(productService.deleteProduct(productId))
            return ResponseEntity.status(HttpStatus.OK).body("Deleted succesfully");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The provided ID couldnt be found");
    }
}