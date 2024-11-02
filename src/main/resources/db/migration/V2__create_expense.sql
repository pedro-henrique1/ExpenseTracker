CREATE TABLE expenses
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    description    VARCHAR(255)   NOT NULL,
    price          DECIMAL(10, 2) NOT NULL,
    date           DATE           NOT NULL,
    payment_method VARCHAR(50)    NOT NULL,
    category       VARCHAR(50)    NOT NULL,
    observation    VARCHAR(255)   NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)