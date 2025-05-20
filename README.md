E-Commerce Microservices Platform

This project is a microservices-based e-commerce platform built with Spring Boot and Spring Cloud. It consists of multiple services working together to provide a complete shopping experience.
Architecture Overview

Architecture Diagram (Placeholder for actual diagram)
Services

    User Service - Gateway service handling authentication, user management, and routing

    Config Server - Centralized configuration management

    Registry Service - Eureka server for service discovery

    Order Service - Manages shopping carts and orders

    Product Service - Handles product catalog and inventory

    Payment Service - Processes payments

    MySQL Databases - Data persistence for each service

Prerequisites

    Java 17+

    Docker 20.10+

    Docker Compose 2.0+

    Maven 3.8+

###Running Locally
1. Change configuration properties to use localhost

2. Build and Run Services

Build all services:

mvn clean package

Run each service in order:

    Config-server

    Registryservice

    Userservice

    Productservice

    Orderservice

    Paymentservice

Example for Userservice:
bash

cd userservice
mvn spring-boot:run

###Running with Docker
1. Update config properties, both on GitHub repository for config server, and in each application properties for the microservices

2. Build and start all services:

mvn clean package

docker build -t app-name .

docker run -p 8080:8080 app-name

For each microservice, changing the port

###Service Ports

Service	Port

Config Server	8888

Registry Service	8761

User Service	8080

Product Service	8081

Order Service	8082

Payment Service	8083


###API Documentation

##User Service Endpoints

#Authentication

    POST /api/auth/login - User login
Body:
{
    "username" : "",
    "password" : ""
}

    POST /api/auth/register - User registration, sends to the e-mail a token

Body:
{
    "username" : "",
    "password" : "",
    "email" : "....@gmail.com"
}

    POST /api/auth/register/admin - Method to register an admin, sends to the e-mail a token

    POST /api/auth/verify-otp - Verify OTP to complete registration and allow login
Body:
{
    "username" : "",
    "mfaOtp" : ######
}


#Products (Public)

    GET /api/products/list - List all products

    GET /api/products/{id} - Get product by ID

    GET /api/products/name/{name} - List products containing the name

    GET /api/products/category/{category} - List products from a category

#User Operations

    GET /api/users/info - Get user info

    POST /api/users/resetpwd method to change password, revokes previous tokens

    POST /api/users/changemail method to change mail, sends new OTP code to verify, if not done in time, it resets the previous mail

    POST /api/users/add-to-cart - Add to cart

Body:
{
"product_id" : 5,
"quantity" : 1
}

    GET /api/users/view-cart - View cart

    DELETE /api/users/remove-from-cart/product/{product_id} remove an item from cart

    DELETE /api/users/empty-cart remove all items from cart

    DELETE /api/users/cancel-order/{order_id} cancells an order, refills products stock

    PUT /api/users/confirm-order confirms cart, moves from "Not completed" to "Processing", awating payment

    PUT /api/users/reduce-unit/product/{product_id} reduce one unit from a product in cart

    PUT /api/users/increase-unit/product/{product_id} adds one unit from a product in cart

    GET /api/users/view-orders View your orders 

    GET /api/users/view-order-details/{order_id} view the products from an order

    POST /api/users/pay-order/{id}/type/{type} method to simulate the payment, ask for the order id and a String as the method to pay

    GET /api/users/view-payments/{id} method to verify a payment

    GET /api/users/payments-list List of payments from a user

    GET /api/users/view-payments/order/{id} view payments for an order

    PUT /api/users/rejected-payment/{id} method to simulate the rejection of a payment

#Admin Operations

    DELETE /api/admin/delete/{id} - Delete user

    DELETE /api/admin/delete/non-verified - Delete non verified users, there is a scheduled service to erase non verified users each hour from db

    DELETE /api/admin/products/delete/{id} - Delete a product

    POST /api/admin/products/add - Add product

    PUT /api/admin/products/refill/{id} - Refill product stock

    PUT /api/admin/products/product/{id}/price/{price} - change product Price

    PUT /api/admin/products/update/{id} - update a product

    DELETE /api/admin/cancel-order/{order_id} cancel an order

    GET /api/admin/view-orders view orders

    GET /api/admin/view-order-details/{order_id} view orders details

    GET /api/admin/view-payments view payments status

    GET /api/admin/view-payments/{id} view payments status

For User and Admin operations, include in authorization the bearer token generated during login

Limit service for resetpwd 3 resets per 5 minutes, and for login 5 logins per 5 minutes


Configurations


orderservice.properties

spring.application.name=orderservice

#local

spring.datasource.url=jdbc:mysql://localhost:3306/ordersdb

#docker

#spring.datasource.url=jdbc:mysql://host.docker.internal:3306/ordersdb

spring.datasource.username=root

spring.datasource.password=admin

spring.datasource.driverClassName=com.mysql.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update

server.port=8082

#local

eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#docker

#eureka.client.service-url.defaultZone=http://host.docker.internal:8761/eureka


paymentservice.properties

spring.application.name=paymentservice

#local

spring.datasource.url=jdbc:mysql://localhost:3306/paymentdb

#docker

#spring.datasource.url=jdbc:mysql://host.docker.internal:3306/paymentdb

spring.datasource.username=root

spring.datasource.password=admin

spring.datasource.driverClassName=com.mysql.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update

server.port=8083

#local

eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#docker

#eureka.client.service-url.defaultZone=http://host.docker.internal:8761/eureka


productservice.properties

spring.application.name=productservice

#local

spring.datasource.url=jdbc:mysql://localhost:3306/productsplatform

#docker

#spring.datasource.url=jdbc:mysql://host.docker.internal:3306/productsplatform

spring.datasource.username=root

spring.datasource.password=admin

spring.datasource.driverClassName=com.mysql.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update

server.port=8081

#local

eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#docker

#eureka.client.service-url.defaultZone=http://host.docker.internal:8761/eureka


registryservice.properties

spring.application.name=registryservice

server.port=8761

eureka.client.register-with-eureka= false

eureka.client.fetch-registry= false


userservice.properties

spring.application.name=userservice

#local

spring.datasource.url=jdbc:mysql://localhost:3306/usersecommercedb

#docker

#spring.datasource.url=jdbc:mysql://host.docker.internal:3306/usersecommercedb

spring.datasource.username=root

spring.datasource.password=admin

spring.datasource.driverClassName=com.mysql.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update

logging.level.root=INFO

logging.file.name=logs/demo-application-dev.log

# JWT Configuration

jwt.secret=YXjxxVVRZbVb47jWEdAVuZS15rQTziuo #Create a key to generate JWT tokens

jwt.expiration=3600000

server.port=8080

spring.mail.host=smtp.gmail.com

spring.mail.port=587

spring.mail.username=<email to send OTP code>

spring.mail.password=<app token to send email from gmail>

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.starttls.enable=true

#local

eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#Docker

#eureka.client.service-url.defaultZone=http://host.docker.internal:8761/eureka


config server properties

spring.application.name=config-server

server.port=8888

spring.cloud.config.server.git.uri=<git repository with config files>

spring.cloud.config.server.git.clone-on-start=true

#branch where files are located
spring.cloud.config.server.git.default-label=master

spring.cloud.config.server.git.allowed-branches=master

spring.cloud.config.server.git.username=<username>

spring.cloud.config.server.git.password=<token generated from github>