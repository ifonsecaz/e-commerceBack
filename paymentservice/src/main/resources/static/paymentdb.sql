create database paymentdb;

use paymentdb;

create table payment(
	payment_id bigint primary key auto_increment,
    amount decimal(9,2),
    order_id bigint,
    payment_date date,
    payment_method varchar(50),
	payment_status varchar(50),
    user_id bigint
);

select * from payment;