create database ordersDB;

use ordersdb;

create table orders(
	order_id bigint primary key auto_increment,
    user_id bigint not null,
    order_date date,
    total_amount decimal(9,2),
    order_status varchar(50)
);

create table orderdetails(
	order_detail_id bigint primary key auto_increment,
    order_id bigint not null,
    product_id bigint,
    quantity int,
    unit_price decimal(9,2),
    foreign key (order_id) references orders (order_id)
);


select * from orders;

select * from orderdetails;
#Types  Not completed not confirmed, To ship after payment, Processing waiting payment, 
SELECT order_id FROM orders where user_id=1 and order_status="Not completed" Order by order_id desc limit 1;