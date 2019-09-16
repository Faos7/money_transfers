DROP TABLE IF EXISTS faos7_money_transfer.end_user;
DROP TABLE IF EXISTS faos7_money_transfer.account;
DROP TABLE IF EXISTS faos7_money_transfer.money_transaction;


DROP SEQUENCE IF EXISTS faos7_money_transfer.s_end_user_id;

CREATE SEQUENCE faos7_money_transfer.s_end_user_id START WITH 1;

CREATE TABLE faos7_money_transfer.end_user (
    id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,

    CONSTRAINT pk_t_end_user PRIMARY KEY (ID)
);

CREATE TABLE faos7_money_transfer.account (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    end_user_id INT NOT NULL,
    balance DECIMAL NOT NULL,

    CONSTRAINT pk_t_account PRIMARY KEY (id),
    CONSTRAINT fk_t_account_end_user_id FOREIGN KEY (end_user_id) REFERENCES faos7_money_transfer.end_user(id)
);

CREATE TABLE faos7_money_transfer.money_transaction (
    id INT NOT NULL AUTO_INCREMENT,
    details VARCHAR(500),
    from_account_id INT,
    to_account_id INT,
    amount DECIMAL NOT NULL,
    created_at TIMESTAMP NOT NULL,


    CONSTRAINT pk_t_money_transaction PRIMARY KEY (id),
    CONSTRAINT fk_t_money_transaction_from_account_id FOREIGN KEY (from_account_id) REFERENCES faos7_money_transfer.account(id),
    CONSTRAINT fk_t_money_transaction_to_account_id FOREIGN KEY (to_account_id) REFERENCES faos7_money_transfer.account(id)
);