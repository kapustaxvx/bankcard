CREATE TABLE IF NOT EXISTS customers
(
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(200) NOT NULL,
    second_name VARCHAR(200) NOT NULL,
    version BIGINT NOT NULL DEFAULT (0)
);

CREATE TABLE IF NOT EXISTS accounts
(
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    debit DECIMAL NOT NULL DEFAULT (0),
    credit DECIMAL NOT NULL DEFAULT (0),
    version BIGINT NOT NULL DEFAULT (0)
);

CREATE TABLE IF NOT EXISTS bank_card_statuses
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS bank_cards
(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(id),
    card_number VARCHAR(16) NOT NULL UNIQUE,
    status_id BIGINT REFERENCES bank_card_statuses(id),
    version BIGINT NOT NULL DEFAULT (0)
);

CREATE TABLE IF NOT EXISTS increase_invoices
(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(id),
    amount DECIMAL NOT NULL,
    creation_date TIMESTAMP
);

CREATE TABLE IF NOT EXISTS decrease_invoices
(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES accounts(id),
    amount DECIMAL NOT NULL,
    creation_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS unconfirmed_increases
(
    increase_id BIGINT REFERENCES increase_invoices(id)
);

CREATE TABLE IF NOT EXISTS unconfirmed_decreases
(
    decrease_id BIGINT REFERENCES decrease_invoices(id)
);

CREATE TABLE IF NOT EXISTS customer_accounts
(
    customer_id BIGINT REFERENCES customers(id) NOT NULL,
    account_id BIGINT REFERENCES accounts(id) NOT NULL UNIQUE ,
    UNIQUE (customer_id, account_id)
);

CREATE TABLE IF NOT EXISTS counterparties
(
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(200) NOT NULL UNIQUE,
    version BIGINT NOT NULL DEFAULT (0)
);

CREATE TABLE IF NOT EXISTS counterparty_accounts
(
    counterparty_id BIGINT REFERENCES counterparties(id) NOT NULL,
    account_id BIGINT REFERENCES accounts(id) NOT NULL UNIQUE,
    UNIQUE (counterparty_id, account_id)
);
