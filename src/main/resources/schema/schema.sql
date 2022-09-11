-- Base
CREATE TABLE IF NOT EXISTS te_account (
    id VARCHAR(36) PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS te_virtual_account (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    identifier VARCHAR(50) NOT NULL UNIQUE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS te_currency (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_singular VARCHAR(50) NOT NULL UNIQUE,
    name_plural VARCHAR(50) NOT NULL UNIQUE,
    symbol VARCHAR(2) NOT NULL,
    num_fraction_digits INT NOT NULL DEFAULT 0,
    is_default BOOL NOT NULL
);

INSERT IGNORE INTO te_currency(id, name_singular, name_plural, symbol, is_default)
VALUES (1, 'Dollar', 'Dollars', '$', 1);

CREATE TABLE IF NOT EXISTS te_default_balance (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    currency_id INT NOT NULL UNIQUE,
    default_balance NUMERIC NOT NULL DEFAULT 0,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS te_balance (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    account_id VARCHAR(36) NOT NULL,
    currency_id INT NOT NULL,
    balance NUMERIC NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES te_account(id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE,
    CONSTRAINT uk_balance UNIQUE(account_id, currency_id)
);