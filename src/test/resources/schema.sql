CREATE TABLE IF NOT EXISTS te_account (
    id VARCHAR(36) PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS te_currency (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_singular VARCHAR(50) NOT NULL UNIQUE,
    name_plural VARCHAR(50) NOT NULL UNIQUE,
    symbol VARCHAR(2) NOT NULL,
    num_fraction_digits INT DEFAULT 0 NOT NULL,
    is_default BOOL NOT NULL
);

CREATE TABLE IF NOT EXISTS te_balance (
    user_id VARCHAR(36) NOT NULL,
    currency_id INT NOT NULL,
    balance NUMERIC DEFAULT 0 NOT NULL,
    FOREIGN KEY (user_id) REFERENCES te_account(id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE,
    UNIQUE(user_id, currency_id)
);