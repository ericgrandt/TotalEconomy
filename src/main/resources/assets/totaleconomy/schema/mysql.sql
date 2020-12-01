CREATE TABLE IF NOT EXISTS te_user (
    id VARCHAR(36) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS te_currency (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_singular VARCHAR(50) NOT NULL UNIQUE,
    name_plural VARCHAR(50) NOT NULL UNIQUE,
    symbol VARCHAR(1) NOT NULL,
    is_default BOOL NOT NULL
);

CREATE TABLE IF NOT EXISTS te_balance (
    user_id VARCHAR(36) NOT NULL REFERENCES te_user(id) ON DELETE CASCADE,
    currency_id INT NOT NULL REFERENCES te_currency(id) ON DELETE CASCADE,
    balance NUMERIC DEFAULT 0 NOT NULL,
    UNIQUE(user_id, currency_id)
);