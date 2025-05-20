create database productsplatform;

use productsplatform;

create table product(
	product_id int primary key auto_increment,
    product_name varchar(50) unique not null,
    category varchar(50),
    description varchar(500),
    price decimal(7,2) not null,
    stock_quantity int
);

INSERT INTO product (product_name, category, description, price, stock_quantity) VALUES
('Wireless Bluetooth Earbuds', 'Electronics', 'High-quality wireless earbuds with noise cancellation', 79.99, 150),
('Smartphone X', 'Electronics', 'Latest model with 6.5" AMOLED display', 899.99, 75),
('4K Ultra HD Smart TV', 'Electronics', '55-inch smart TV with HDR support', 649.99, 30),
('Stainless Steel Cookware Set', 'Home & Kitchen', '10-piece non-stick cookware set', 129.95, 3),
('Air Fryer Oven', 'Home & Kitchen', 'Digital air fryer with 8 cooking functions', 89.99, 60),
('Memory Foam Pillow', 'Home & Kitchen', 'Orthopedic pillow for neck support', 39.95, 120),
('Men\'s Running Shoes', 'Clothing', 'Lightweight running shoes with cushioned soles', 69.99, 4),
('Women\'s Winter Jacket', 'Clothing', 'Waterproof jacket with thermal insulation', 119.99, 1),
('Unisex Cotton T-Shirt', 'Clothing', 'Breathable 100% cotton t-shirt', 19.99, 200),
('The Silent Patient', 'Books', 'Bestselling psychological thriller', 12.99, 55),
('Atomic Habits', 'Books', 'Guide to building good habits', 14.95, 90),
('Python Crash Course', 'Books', 'Hands-on Python programming guide', 29.99, 35),
('Yoga Mat', 'Sports & Outdoors', 'Non-slip eco-friendly yoga mat', 24.99, 70),
('Camping Tent', 'Sports & Outdoors', '4-person waterproof tent', 149.99, 25),
('Adjustable Dumbbell Set', 'Sports & Outdoors', 'Pair of dumbbells with 5-25kg range', 199.99, 15),
('Wireless Keyboard & Mouse', 'Office Supplies', 'Ergonomic keyboard and mouse combo', 49.95, 110),
('Desk Organizer', 'Office Supplies', 'Multi-compartment desktop organizer', 17.99, 80),
('Sticky Notes 100-sheet Pack', 'Office Supplies', 'Assorted color sticky notes', 4.99, 300),
('Vitamin C Serum', 'Beauty', 'Anti-aging facial serum', 29.95, 1),
('Electric Toothbrush', 'Beauty', 'Rechargeable toothbrush with 3 modes', 59.99, 50);

select * from product;