package com.ecommerce.userservice.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.RateLimiterService;
import com.ecommerce.userservice.service.EmailService;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.entity.EmailChangeRequest;
import com.ecommerce.userservice.entity.OrdersDTO;
import com.ecommerce.userservice.entity.OrderdetailsDTO;
import com.ecommerce.userservice.entity.PasswordResetRequest;
import com.ecommerce.userservice.entity.PaymentDTO;
import com.ecommerce.userservice.entity.ProductDTO;
import com.ecommerce.userservice.entity.ProductNoIdDTO;
import com.ecommerce.userservice.entity.ProductcartDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/users")
public class ApiUserController {
    private final UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private RateLimiterService rateLimiterService;
    @Autowired
    private EmailService emailService;


    public ApiUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user=userRepository.findByUsername(username);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/resetpwd")
    public ResponseEntity<?> getUserInfo(@RequestBody PasswordResetRequest req, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(ip,"resetpwd");

        if (bucket.tryConsume(1)) {
            if (!req.new_password.equals(req.confirm_password)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords don't match");
            }

            if (req.new_password.length() < 8) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password too short");
            }

            Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
            String username = authenticationName.getName();

            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, req.password)
                );
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password is incorrect");
            }

            User user=userRepository.findByUsername(username);
            user.setPassword(encoder.encode(req.new_password));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("Password was updated");
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many attempts");
        }
        
    }
        
    //Need logic to not make the mail change if account is not validated
    @PostMapping("/changemail")
    public ResponseEntity<?> getUserInfo(@RequestBody EmailChangeRequest req, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(ip,"resetpwd");

        if (bucket.tryConsume(1)) {
            Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
            String username = authenticationName.getName();

            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, req.password)
                );
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password is incorrect");
            }

            User user=userRepository.findByUsername(username);
            user.setOldEmail(user.getEmail());
            user.setEmail(req.email);
            user.setAcvalidated(false);
            user.setLastPasswordReset(); //Used to check when last change was made
            userRepository.save(user); 

            String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit
            user.setMfaOtp(otp);
            user.setMfaOtpExpiry(LocalDateTime.now().plusMinutes(5));

            // Send OTP via email
            emailService.sendOtp(user.getEmail(), otp);

            return ResponseEntity.status(HttpStatus.OK).body("Email was updated, please verify your mail");
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many attempts");
        }
    }


    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addProductToCart(@RequestBody ProductcartDTO product) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "http://localhost:8082/orders/addproduct";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);
        product.user_id=user.getUser_id();
        product.unit_price=0;

        HttpEntity<ProductcartDTO> entity = new HttpEntity<>(product,headers);

        try{
            return restTemplate.postForEntity(uri, entity, OrdersDTO.class);
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

    @DeleteMapping("/remove-from-cart/product/{product_id}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable("product_id") long productID) {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/remove/user/"+user.getUser_id()+"/product/"+productID;

        try{
            return restTemplate.exchange(uri, HttpMethod.DELETE, null, OrdersDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @DeleteMapping("/empty-cart")
    public ResponseEntity<?> emptyCart() {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/empty/"+user.getUser_id();

        return restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
    }

    @DeleteMapping("/cancel-order/{order_id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("order_id") long order_id) {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username); 

        String uri = "http://localhost:8082/orders/cancel/user/"+user.getUser_id()+"/order/"+order_id;
        
        return restTemplate.exchange(uri, HttpMethod.DELETE, null, String.class);
    }

    @PutMapping("/confirm-order")
    public ResponseEntity<?> confirmCart() {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/complete-order/"+user.getUser_id();

        try{
            return restTemplate.exchange(uri, HttpMethod.PUT, null, OrdersDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @PutMapping("/reduce-unit/product/{product_id}")
    public ResponseEntity<?> reduceProductCart(@PathVariable("product_id") long product_id) {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/reduce-product/user/"+user.getUser_id()+"/product/"+product_id;

        try{
            return restTemplate.exchange(uri, HttpMethod.PUT, null, OrdersDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @PutMapping("/increase-unit/product/{product_id}")
    public ResponseEntity<?> increaseProductCart(@PathVariable("product_id") long product_id) {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/increment-product/user/"+user.getUser_id()+"/product/"+product_id;

        try{
            return restTemplate.exchange(uri, HttpMethod.PUT, null, OrdersDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @GetMapping("/view-cart")
    public ResponseEntity<?> viewCart() {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/view-cart/"+user.getUser_id();

        try{
            return restTemplate.exchange(uri, HttpMethod.GET, null, OrdersDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @GetMapping("/view-orders")
    public ResponseEntity<?> viewOrderList() {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/orderslist/"+user.getUser_id();
        
        ResponseEntity<List<OrdersDTO>> response = restTemplate.exchange(uri,HttpMethod.GET,null,new ParameterizedTypeReference<List<OrdersDTO>>() {});

        return response;
    }

    @GetMapping("/view-order-details/{order_id}")
    public ResponseEntity<?> viewOrderDetails(@PathVariable("order_id") long order_id) {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8082/orders/ordersdetails/users/"+user.getUser_id()+"/order/"+order_id;
        
        ResponseEntity<List<OrderdetailsDTO>> response = restTemplate.exchange(uri,HttpMethod.GET,null,new ParameterizedTypeReference<List<OrderdetailsDTO>>() {});

        return response;
    }

    //payments call complete payment for orders
    @PostMapping("/pay-order/{id}/type/{type}")
    public ResponseEntity<?> savePayment(@PathVariable("id") Long orderId, @PathVariable("type") String method)
    {
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);
        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8082/orders/view-order/"+orderId;

        try{
            OrdersDTO orderD=restTemplate.exchange(uri, HttpMethod.GET, null, OrdersDTO.class).getBody();
            if(orderD.getUser_id()==user.getUser_id()){
                PaymentDTO aux=new PaymentDTO();
                aux.setAmount(orderD.getTotal_amount());
                aux.setOrderId(orderId);
                aux.setPayment_date(LocalDate.now());
                aux.setPayment_method(method);
                aux.setPayment_status("Processed");
                aux.setUserId(user.getUser_id());

                restTemplate = new RestTemplate();
                uri = "http://localhost:8083/payments/pay";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<PaymentDTO> entity = new HttpEntity<>(aux,headers);

                try{
                    ResponseEntity<PaymentDTO> res=restTemplate.exchange(uri,HttpMethod.POST, entity, PaymentDTO.class);
                    if(res.getStatusCode().equals(HttpStatus.CREATED)){
                        restTemplate = new RestTemplate();

                        uri = "http://localhost:8082/orders/complete-payment/"+res.getBody().getOrderId();
                        restTemplate.exchange(uri,HttpMethod.PUT, null, OrdersDTO.class);
                        
                    }
                    return res;

                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                    String errorBody = ex.getResponseBodyAsString();
                    HttpStatusCode statusCode = ex.getStatusCode();

                    return ResponseEntity.status(statusCode).body(errorBody);
                } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
                }
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect order ID");
            }
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @GetMapping("/view-payments/{id}")
    public ResponseEntity<?> viewPayments(@PathVariable("id") Long paymentId) {
        RestTemplate restTemplate = new RestTemplate();
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8083/payments/payment/" + paymentId;
        
        try{
            ResponseEntity<PaymentDTO> aux= restTemplate.postForEntity(uri, null, PaymentDTO.class);
            if(aux.getBody().getUserId()==user.getUser_id()){
                return aux;
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't have permission");
            }
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

    
    @GetMapping("/payments-list")
    public ResponseEntity<?> viewPayments() {
        RestTemplate restTemplate = new RestTemplate();
        
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8083/payments/paymentlist/user/"+user.getUser_id();
        
        ResponseEntity<List<PaymentDTO>> response = restTemplate.exchange(uri,HttpMethod.GET,null,new ParameterizedTypeReference<List<PaymentDTO>>() {});

        return response;
    }

    @GetMapping("/view-payments/order/{id}")
    public ResponseEntity<?> viewPaymentsByOrder(@PathVariable("id") Long orderId) {
        RestTemplate restTemplate = new RestTemplate();
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        String uri = "http://localhost:8083/payments/payment/order/" + orderId;
        
        try{
            ResponseEntity<PaymentDTO> aux= restTemplate.postForEntity(uri, null, PaymentDTO.class);
            if(aux.getBody().getUserId()==user.getUser_id()){
                return aux;
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You don't have permission");
            }
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

    @PutMapping("/rejected-payment/{id}")
    public ResponseEntity<?> savePayment(@PathVariable("id") Long paymentID)
    {
        Authentication authenticationName = SecurityContextHolder.getContext().getAuthentication();
        String username = authenticationName.getName();
        User user=userRepository.findByUsername(username);

        RestTemplate restTemplate = new RestTemplate();

        String uri = "http://localhost:8083/payments/reject/"+paymentID;

        try{
            ResponseEntity<PaymentDTO> res=restTemplate.exchange(uri,HttpMethod.PUT, null, PaymentDTO.class);
            
            restTemplate = new RestTemplate();

            uri = "http://localhost:8082/orders/rejected-payment/"+res.getBody().getOrderId();
            restTemplate.exchange(uri,HttpMethod.PUT, null, OrdersDTO.class);

            return res;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            String errorBody = ex.getResponseBodyAsString();
            HttpStatusCode statusCode = ex.getStatusCode();

            return ResponseEntity.status(statusCode).body(errorBody);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    //Need logic change
    /*
    @PutMapping("/products/product/{id}/unitsbuy/{units}")
    public ResponseEntity<?>
    buyProduct(@PathVariable("id") Long productId,
                        @PathVariable("units") int units)
    {
        final String uri = "http://localhost:8081/products/update/product/"+productId+"/unitsbuy/"+units;
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
    */
}