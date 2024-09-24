CREATE TABLE merchant (
    id VARCHAR AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    FOREIGN KEY (merchant_id)
);

CREATE TABLE customer (
    id VARCHAR AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    FOREIGN KEY (customer_id)
);


CREATE TABLE products (
    id VARCHAR AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    price DECIMAL(10, 2),
    merchant_id BIGINT,
    FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE TABLE transaction (
    id VARCHAR AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR (100),
    customer_id VARCHAR (100),
    date TIMESTAMP,
    reward_points INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE rewards (
    id VARCHAR AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    points_required INT
);