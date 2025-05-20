create database usersecommercedb;

use usersecommercedb;

create table user(
	user_id bigint primary key auto_increment,
    username varchar(50) unique not null,
    password varchar(100) not null,
    last_password_reset Date,
    otp varchar(6),
    otp_expiry Date,
    email varchar(100) unique not null,
    account_validated boolean default false,
    old_email varchar(100)
);

create table role(
	role_id int primary key auto_increment,
    role varchar(50) not null
);

create table user_role(
	role_id int,
    user_id bigint,
    primary key(role_id,user_id),
    foreign key (role_id) references role(role_id),
    foreign key (user_id) references user(user_id)
);

INSERT INTO role (role) VALUES
('ADMIN'),
('MANAGER'),
('USER'),
('GUEST');

select * from role;

select * from user;

select * from user_role;

select user.*, role.* from user inner join user_role on user.user_id=user_role.user_id inner join role on user_role.role_id=role.role_id;

SELECT * FROM user WHERE account_validated=0;