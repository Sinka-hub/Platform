CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       phone VARCHAR(20) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       nickname VARCHAR(50) NOT NULL,
                       avatar VARCHAR(255),
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       last_login_at DATETIME NULL,
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP
);