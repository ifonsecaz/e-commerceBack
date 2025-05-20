package com.ecommerce.userservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.ecommerce.userservice.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.entity.OrderdetailsDTO;
import com.ecommerce.userservice.entity.OrdersDTO;
import com.ecommerce.userservice.entity.PaymentDTO;
import com.ecommerce.userservice.entity.ProductDTO;
import com.ecommerce.userservice.entity.ProductNoIdDTO;



@RestController
@RequestMapping("/api/admin")
public class ApiAdminController {
    private final UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;

    public ApiAdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //requires sending the JWT token on authorization header
    //Bearer token
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        User aux=userRepository.findById(id).get();
        aux.removeRole();
        userRepository.delete(aux);
        return ResponseEntity.ok("User deleted");
    }

    //delete non verified
    //should be automated
    //@Transactional //Restcontroller are transactional by default
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/non-verified")
    public ResponseEntity<?> deleteNonVerified() {
        List<User> aux=userRepository.findByAcvalidatedFalse();
        for(User user : aux){
            if(user.getOldEmail()!=null){
                user.setEmail(user.getOldEmail());
                user.setAcvalidated(true);
                user.setLastPasswordReset();
                userRepository.save(user);
            }
            else{
                user.removeRole();
                userRepository.delete(user);
            }
        }
        return ResponseEntity.ok("Completed");
    }

    //delete product
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        final String uri = "http://localhost:8081/products/delete/" + id;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(uri, HttpMethod.DELETE,null, String.class);
    }

    //add units
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/products/refill/{id}/units/{units}")
    public ResponseEntity<?> refillProduct(@PathVariable Long id,@PathVariable int units) {
        final String uri = "http://localhost:8081/products/update/product/"+id+"/refill/" + units;
        RestTemplate restTemplate = new RestTemplate();
        
        try{
            return restTemplate.exchange(uri, HttpMethod.PUT, null, ProductDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Handle error response as String
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            // Catch all other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    //change price
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/products/product/{id}/price/{price}")
    public ResponseEntity<?> changePrice(@PathVariable Long id,@PathVariable double price) {
        final String uri = "http://localhost:8081/products/update/product/"+id+"/price/"+price;
        RestTemplate restTemplate = new RestTemplate();
        try{
            return restTemplate.exchange(uri, HttpMethod.PUT, null, ProductDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Handle error response as String
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            // Catch all other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
        
    }

    //update product
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/products/update/{id}")
    public ResponseEntity<?> refillProduct(@PathVariable Long id,@RequestBody ProductNoIdDTO product) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "http://localhost:8081/products/update/product/"+id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductNoIdDTO> entity = new HttpEntity<>(product,headers);

        try{
            return restTemplate.exchange(uri,HttpMethod.PUT, entity, ProductDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Handle error response as String
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            // Catch all other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    //add product
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/products/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductNoIdDTO product) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "http://localhost:8081/products/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductNoIdDTO> entity = new HttpEntity<>(product,headers);

        try{
            return restTemplate.postForEntity(uri, entity, ProductDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Handle error response as String
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            // Catch all other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/cancel-order/{order_id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("order_id") long order_id) {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8082/orders/cancel/order/"+order_id;
        
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/view-orders")
    public ResponseEntity<?> viewOrderList() {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8082/orders/orderslist";
        
        ResponseEntity<List<OrdersDTO>> response = restTemplate.exchange(uri,HttpMethod.GET,null,new ParameterizedTypeReference<List<OrdersDTO>>() {});

        return response;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/view-order-details/{order_id}")
    public ResponseEntity<?> viewOrderDetails(@PathVariable("order_id") long order_id) {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8082/orders/ordersdetails/"+order_id;
        
        ResponseEntity<List<OrderdetailsDTO>> response = restTemplate.exchange(uri,HttpMethod.GET,null,new ParameterizedTypeReference<List<OrderdetailsDTO>>() {});

        return response;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/view-payments")
    public ResponseEntity<?> viewPayments() {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8083/payments/paymentlist/";
        
        ResponseEntity<List<PaymentDTO>> response = restTemplate.exchange(uri,HttpMethod.GET,null,new ParameterizedTypeReference<List<PaymentDTO>>() {});

        return response;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/view-payments/{id}")
    public ResponseEntity<?> viewPayments(@PathVariable("id") Long paymentId) {
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8083/payments/payment/" + paymentId;
        
        try{
            return restTemplate.postForEntity(uri, null, PaymentDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Handle error response as String
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            // Catch all other unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }
}