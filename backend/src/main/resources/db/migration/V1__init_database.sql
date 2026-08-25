CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       role VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       phone VARCHAR(20),
                       email VARCHAR(100) NOT NULL UNIQUE,
                       status VARCHAR(20) NOT NULL,
                       role_id BIGINT NOT NULL,

                       CONSTRAINT fk_user_role
                           FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE categories (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description VARCHAR(255),
                            status VARCHAR(20) NOT NULL
);

CREATE TABLE suppliers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20),
                           email VARCHAR(100),
                           address VARCHAR(255),
                           status VARCHAR(20) NOT NULL
);

CREATE TABLE products (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          name VARCHAR(100) NOT NULL,
                          price BIGINT NOT NULL,
                          quantity INT NOT NULL,
                          image_url VARCHAR(255),
                          status VARCHAR(20) NOT NULL,
                          category_id BIGINT NOT NULL,
                          supplier_id BIGINT NOT NULL,

                          CONSTRAINT fk_product_category
                              FOREIGN KEY (category_id) REFERENCES categories(id),

                          CONSTRAINT fk_product_supplier
                              FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE customers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20),
                           email VARCHAR(100),
                           address VARCHAR(255),
                           status VARCHAR(20) NOT NULL
);

CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        order_code VARCHAR(100) NOT NULL UNIQUE,
                        order_date DATETIME NOT NULL,
                        total_amount BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        customer_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,

                        CONSTRAINT fk_order_customer
                            FOREIGN KEY (customer_id) REFERENCES customers(id),

                        CONSTRAINT fk_order_user
                            FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_details (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               quantity INT NOT NULL,
                               unit_price BIGINT NOT NULL,
                               subtotal BIGINT NOT NULL,
                               order_id BIGINT NOT NULL,
                               product_id BIGINT NOT NULL,

                               CONSTRAINT fk_detail_order
                                   FOREIGN KEY (order_id) REFERENCES orders(id),

                               CONSTRAINT fk_detail_product
                                   FOREIGN KEY (product_id) REFERENCES products(id)
);