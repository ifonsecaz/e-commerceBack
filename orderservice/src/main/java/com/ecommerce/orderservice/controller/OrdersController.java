package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.entity.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.orderservice.service.OrdersService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired 
    private OrdersService ordersService;

    private OrderDTO mapOrder(Orders order){
        OrderDTO res =new OrderDTO(order.getOrder_id(), order.getUser_id(), order.getOrder_date(), order.getTotal_amount(),order.getOrder_status());
        return res;
    }
    
    private OrderdetailsDTO mapOrderDetails(Orderdetails order){
        OrderdetailsDTO res= new OrderdetailsDTO(order.getOrder_detail_id(), order.getProduct_id(),order.getQuantity(),order.getUnit_price());
        return res;
    }

    //M review enough product units
    @PostMapping("/addproduct")
    public ResponseEntity<?> saveToCart(
        @Valid @RequestBody ProductDTO product)
    {
        String uri = "http://localhost:8081/products/product/"+product.product_id+"/units";
        RestTemplate restTemplate = new RestTemplate();
        int availableUnits=restTemplate.getForEntity(uri, Integer.class).getBody();

        

        if(availableUnits>=product.quantity){
            uri = "http://localhost:8081/products/product/"+product.product_id+"/price";
            restTemplate = new RestTemplate();
            double price=restTemplate.getForEntity(uri, Integer.class).getBody();
            product.unit_price=price;
            Orders res=ordersService.addProduct(product);
            if(res!=null){
                return ResponseEntity.status(HttpStatus.CREATED).body(mapOrder(res));
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Verify the data");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Sorry, we don't have enough units"); 
        }
    }

    @DeleteMapping("/remove/user/{id}/product/{product_id}")
    public ResponseEntity<?> removeProduct(@PathVariable("id")
                                       Long user_id, @PathVariable("product_id")
                                       Long productId)
    {
        Orders res=ordersService.removeProduct(user_id, productId);
        if(res==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your cart is empty");
        }
        return ResponseEntity.status(HttpStatus.OK).body(mapOrder(res));
    }


    @DeleteMapping("/empty/{id}")
    public ResponseEntity<?> emptyCart(@PathVariable("id")
                                       Long userId)
    {
        ordersService.emptyCart(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Cart is empty");
    }

    //refill product units
    @DeleteMapping("/cancel/user/{id}/order/{order_id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("id")
                                       Long user_id, @PathVariable("order_id")
                                       Long order_id)
    {
        List<Orderdetails> aux = ordersService.fetchOrdersDetailList(user_id, order_id); //this will verify user coincides with the order
        for(Orderdetails item:aux){
            final String uri = "http://localhost:8081/products/update/product/"+item.getProduct_id()+"/refill/"+item.getQuantity();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(uri, HttpMethod.PUT, null, Object.class);
        }
        if(ordersService.cancelOrder(user_id, order_id))
                return ResponseEntity.status(HttpStatus.OK).body("Order canceled");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("We couldn't find the order");
    }

    //refill product units
    @DeleteMapping("/cancel/order/{id}")
    public ResponseEntity<?> cancelOrderAdmin(@PathVariable("id")
                                       Long orderID)
    {
        List<Orderdetails> aux = ordersService.fetchOrdersDetailListAdmin(orderID);
        for(Orderdetails item:aux){
            final String uri = "http://localhost:8081/products/update/product/"+item.getProduct_id()+"/refill/"+item.getQuantity();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(uri, HttpMethod.PUT, null, Object.class);
        }
        if(ordersService.cancelOrderAdmin(orderID))
                return ResponseEntity.status(HttpStatus.OK).body("Order canceled");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("We couldn't find the order");
    }

    //buy product units, need extra logic in case order not processed a product cant be placed and to refill all products
    @PutMapping("/complete-order/{id}")
    public ResponseEntity<?>
    completeOrder(@PathVariable("id") Long userID)
    {
        long order_id=ordersService.viewCart(userID);
        List<Orderdetails> aux = ordersService.fetchOrdersDetailList(userID, order_id);
        for(Orderdetails item:aux){
            final String uri = "http://localhost:8081/products/update/product/"+item.getProduct_id()+"/unitsbuy/"+item.getQuantity();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(uri, HttpMethod.PUT, null, Object.class);
        }
        Orders res=ordersService.completeOrder(userID);
        if(res==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your cart is empty");
        return ResponseEntity.status(HttpStatus.OK).body(mapOrder(res));
    }

    //called from payments
    @PutMapping("/complete-payment/{id}")
    public ResponseEntity<?>
    completePayment(@PathVariable("id") Long orderID)
    {
        Orders res=ordersService.completePaymentOrder(orderID);
        if(res==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("We couldn't find the order");
        return ResponseEntity.status(HttpStatus.OK).body(mapOrder(res));
    }

    @PutMapping("/rejected-payment/{id}")
    public ResponseEntity<?>
    rejectedPayment(@PathVariable("id") Long orderID)
    {
        Orders res=ordersService.rejectedPaymentOrder(orderID);
        if(res==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("We couldn't find the order");
        return ResponseEntity.status(HttpStatus.OK).body(mapOrder(res));
    }

    @PutMapping("/reduce-product/user/{id}/product/{product_id}")
    public ResponseEntity<?>
    reduceProduct(@PathVariable("id") Long userID,@PathVariable("product_id") Long productID)
    {
        Orders res=ordersService.reduceProduct(userID, productID);
        if(res==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your cart is empty");
        return ResponseEntity.status(HttpStatus.OK).body(mapOrder(res));
    }



    //check stock
    @PutMapping("/increment-product/user/{id}/product/{product_id}")
    public ResponseEntity<?>
    incrementProduct(@PathVariable("id") Long userID,@PathVariable("product_id") Long productID)
    {
        final String uri = "http://localhost:8081/products/product/"+productID+"/units";
        RestTemplate restTemplate = new RestTemplate();
        int availableUnits=restTemplate.getForEntity(uri, Integer.class).getBody();
        long order_id=ordersService.viewCart(userID);
        List<Orderdetails> aux = ordersService.fetchOrdersDetailList(userID, order_id);
        boolean canIncrease=false;
        for(Orderdetails item:aux){
            if(item.getProduct_id()==productID){
                if(item.getQuantity()+1<=availableUnits){
                    canIncrease=true;
                    break;
                }
            }
        }
        if(!canIncrease)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough units");
        Orders res=ordersService.increaseProduct(userID, productID);
        if(res==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your cart is empty");
        return ResponseEntity.status(HttpStatus.OK).body(mapOrder(res));
    }

    @GetMapping("/orderslist")
    public List<OrderDTO> fetchOrderList()
    {
        List<OrderDTO> res=new ArrayList<>();
        List<Orders> aux=ordersService.fetchOrdersList();
        if(aux!=null){
            for(Orders order:aux){
                res.add(mapOrder(order));
            }
        }
        return res;
    }

    @GetMapping("/view-cart/{id}")
    public ResponseEntity<?> viewCart(@PathVariable("id") Long userID)
    {
        long order_id=ordersService.viewCart(userID);
        if(order_id>0){
            return ResponseEntity.status(HttpStatus.OK).body(mapOrder(ordersService.fetchOrder(order_id)));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your cart is empty");
    }

    //get one order
    @GetMapping("/view-order/{id}")
    public ResponseEntity<?> viewOrder(@PathVariable("id") Long order_id)
    {
        Orders aux=ordersService.fetchOrder(order_id);
        if(aux!=null){
            return ResponseEntity.status(HttpStatus.OK).body(mapOrder(aux));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("We couldn't find the ID");
    }


    @GetMapping("/orderslist/{id}")
    public List<OrderDTO> fetchOrderList(@PathVariable("id") Long userID)
    {
        List<OrderDTO> res=new ArrayList<>();
        List<Orders> aux=ordersService.fetchUsersOrdersList(userID);
        if(aux!=null){
            for(Orders order:aux){
                res.add(mapOrder(order));
            }
        }
        return res;
    }

    @GetMapping("/ordersdetails/{id}")
    public List<OrderdetailsDTO> fetchOrderDetailsAdmin(@PathVariable("id") Long orderID)
    {
        List<OrderdetailsDTO> res=new ArrayList<>();
        List<Orderdetails> aux=ordersService.fetchOrdersDetailListAdmin(orderID);
        if(aux!=null){
            for(Orderdetails order:aux){
                res.add(mapOrderDetails(order));
            }
        }
        return res;
    }

    @GetMapping("/ordersdetails/users/{id}/order/{order_id}")
    public List<OrderdetailsDTO> fetchOrderDetails(@PathVariable("id") Long userID, @PathVariable("order_id") Long orderID)
    {
        List<OrderdetailsDTO> res=new ArrayList<>();
        List<Orderdetails> aux=ordersService.fetchOrdersDetailList(userID, orderID);
        if(aux!=null){
            for(Orderdetails order:aux){
                res.add(mapOrderDetails(order));
            }
        }
        return res;
    }
}
