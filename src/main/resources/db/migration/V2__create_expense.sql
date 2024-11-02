CREATE TABLE expenses
(
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    description    VARCHAR(255)                                                                                 NOT NULL,
    price          DECIMAL(10, 2)                                                                               NOT NULL,
    date           DATE                                                                                         NOT NULL,
    payment_method ENUM ('Dinheiro', 'Cartão de Crédito', 'Cartão de Débito',
        'Transferência Bancária', 'Pix', 'Boleto', 'Aplicativos de Pagamento','Cheque', 'Cartão de Benefícios') NOT NULL,
    category       ENUM ('Alimentação', 'Transporte', 'Lazer', 'Saúde', 'Moradia',
        'Educação', 'Outros')                                                                                   NOT NULL,
    observation    VARCHAR(255)                                                                                 NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

)