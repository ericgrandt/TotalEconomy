-- Base
CREATE TABLE IF NOT EXISTS te_account (
    id VARCHAR(36) PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS te_currency (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_singular VARCHAR(50) NOT NULL UNIQUE,
    name_plural VARCHAR(50) NOT NULL UNIQUE,
    symbol VARCHAR(2) CHARACTER SET UTF8MB4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    num_fraction_digits INT DEFAULT 0 NOT NULL,
    is_default BOOL NOT NULL
);

INSERT IGNORE INTO te_currency(id, name_singular, name_plural, symbol, is_default)
VALUES (1, 'Dollar', 'Dollars', '$', 1);

CREATE TABLE IF NOT EXISTS te_default_balance (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    currency_id INT NOT NULL UNIQUE,
    default_balance NUMERIC DEFAULT 0 NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE
);

INSERT IGNORE INTO te_default_balance(currency_id, default_balance)
VALUES (1, 100);

CREATE TABLE IF NOT EXISTS te_balance (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    account_id VARCHAR(36) NOT NULL,
    currency_id INT NOT NULL,
    balance NUMERIC DEFAULT 0 NOT NULL,
    FOREIGN KEY (account_id) REFERENCES te_account(id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE,
    CONSTRAINT uk_balance UNIQUE(account_id, currency_id)
);