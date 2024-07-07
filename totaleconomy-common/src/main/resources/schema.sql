CREATE TABLE IF NOT EXISTS te_account (
    id VARCHAR(36) PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS te_currency (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name_singular VARCHAR(50) NOT NULL UNIQUE,
    name_plural VARCHAR(50) NOT NULL UNIQUE,
    symbol VARCHAR(2) NOT NULL,
    num_fraction_digits INT NOT NULL DEFAULT 2,
    is_default BOOL NOT NULL
);

INSERT IGNORE INTO te_currency(id, name_singular, name_plural, symbol, is_default)
VALUES (1, 'Dollar', 'Dollars', '$', 1);

CREATE TABLE IF NOT EXISTS te_default_balance (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    currency_id INT NOT NULL UNIQUE,
    default_balance DECIMAL(38, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE
);

INSERT IGNORE INTO te_default_balance (currency_id, default_balance)
VALUES (1, 100.00);

CREATE TABLE IF NOT EXISTS te_balance (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    account_id VARCHAR(36) NOT NULL,
    currency_id INT NOT NULL,
    balance DECIMAL(38, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES te_account(id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE,
    CONSTRAINT uk_balance UNIQUE(account_id, currency_id)
);

CREATE TABLE IF NOT EXISTS te_job (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    job_name VARCHAR(36) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS te_job_action (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    action_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS te_job_reward (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    job_id VARCHAR(36) NOT NULL,
    job_action_id VARCHAR(36) NOT NULL,
    currency_id INT NOT NULL,
    material VARCHAR(100) NOT NULL,
    money DECIMAL(38, 2) NOT NULL,
    experience INT UNSIGNED NOT NULL,
    FOREIGN KEY (job_id) REFERENCES te_job(id) ON DELETE CASCADE,
    FOREIGN KEY (job_action_id) REFERENCES te_job_action(id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES te_currency(id) ON DELETE CASCADE,
    CONSTRAINT uk_job_reward UNIQUE(job_id, job_action_id, material)
);

CREATE TABLE IF NOT EXISTS te_job_experience (
    id VARCHAR(36) PRIMARY KEY DEFAULT (uuid()),
    account_id VARCHAR(36) NOT NULL,
    job_id VARCHAR(36) NOT NULL,
    experience INT UNSIGNED NOT NULL DEFAULT 0,
    FOREIGN KEY (account_id) REFERENCES te_account(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES te_job(id) ON DELETE CASCADE,
    CONSTRAINT uk_job_experience UNIQUE(account_id, job_id)
);

-- Insert break action
INSERT IGNORE INTO te_job_action(action_name) VALUES ('break');
SET @break_action_id = (SELECT id FROM te_job_action WHERE action_name = 'break');

-- Insert Miner job and rewards
INSERT IGNORE INTO te_job(job_name) VALUES ('Miner');
SET @miner_job_id = (SELECT id FROM te_job WHERE job_name = 'Miner');

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:coal_ore', 0.50, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_coal_ore', 0.50, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:copper_ore', 0.10, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_copper_ore', 0.10, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:iron_ore', 1.00, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_iron_ore', 1.00, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:gold_ore', 2.50, 15);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_gold_ore', 2.50, 15);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:nether_gold_ore', 2.50, 15);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:lapis_ore', 2.00, 15);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_lapis_ore', 2.00, 15);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:diamond_ore', 5.00, 25);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_diamond_ore', 5.00, 25);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:redstone_ore', 2.00, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_redstone_ore', 2.00, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:emerald_ore', 5.00, 30);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:deepslate_emerald_ore', 5.00, 30);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@miner_job_id, @break_action_id, 1, 'minecraft:nether_quartz_ore', 0.10, 1);

-- Insert Lumberjack job and rewards
INSERT IGNORE INTO te_job(job_name) VALUES ('Lumberjack');
SET @lumberjack_job_id = (SELECT id FROM te_job WHERE job_name = 'Lumberjack');

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@lumberjack_job_id, @break_action_id, 1, 'minecraft:oak_log', 0.10, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@lumberjack_job_id, @break_action_id, 1, 'minecraft:birch_log', 0.10, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@lumberjack_job_id, @break_action_id, 1, 'minecraft:spruce_log', 0.10, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@lumberjack_job_id, @break_action_id, 1, 'minecraft:jungle_log', 0.10, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@lumberjack_job_id, @break_action_id, 1, 'minecraft:acacia_log', 0.10, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@lumberjack_job_id, @break_action_id, 1, 'minecraft:dark_oak_log', 0.10, 5);

-- Insert kill action
INSERT IGNORE INTO te_job_action(action_name) VALUES ('kill');
SET @kill_action_id = (SELECT id FROM te_job_action WHERE action_name = 'kill');

-- Insert Hunter job and rewards
INSERT IGNORE INTO te_job(job_name) VALUES ('Hunter');
SET @hunter_job_id = (SELECT id FROM te_job WHERE job_name = 'Hunter');

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@hunter_job_id, @kill_action_id, 1, 'minecraft:chicken', 2.50, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@hunter_job_id, @kill_action_id, 1, 'minecraft:rabbit', 2.50, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@hunter_job_id, @kill_action_id, 1, 'minecraft:cow', 2.50, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@hunter_job_id, @kill_action_id, 1, 'minecraft:pig', 2.50, 10);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@hunter_job_id, @kill_action_id, 1, 'minecraft:sheep', 2.50, 10);

-- Insert fish action
INSERT IGNORE INTO te_job_action(action_name) VALUES ('fish');
SET @fish_action_id = (SELECT id FROM te_job_action WHERE action_name = 'fish');

-- Insert Fisher job and rewards
INSERT IGNORE INTO te_job(job_name) VALUES ('Fisher');
SET @fisher_job_id = (SELECT id FROM te_job WHERE job_name = 'Fisher');

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@fisher_job_id, @fish_action_id, 1, 'minecraft:cod', 5, 20);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@fisher_job_id, @fish_action_id, 1, 'minecraft:salmon', 10, 40);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@fisher_job_id, @fish_action_id, 1, 'minecraft:pufferfish', 20, 80);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@fisher_job_id, @fish_action_id, 1, 'minecraft:tropical_fish', 40, 160);

-- Insert place action
INSERT IGNORE INTO te_job_action(action_name) VALUES ('place');
SET @place_action_id = (SELECT id FROM te_job_action WHERE action_name = 'place');

-- Insert Farmer job and rewards
INSERT IGNORE INTO te_job(job_name) VALUES ('Farmer');
SET @farmer_job_id = (SELECT id FROM te_job WHERE job_name = 'Farmer');

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @place_action_id, 1, 'minecraft:wheat_seeds', 0.01, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @place_action_id, 1, 'minecraft:melon_seeds', 0.01, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @place_action_id, 1, 'minecraft:pumpkin_seeds', 0.01, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @place_action_id, 1, 'minecraft:beetroot_seeds', 0.01, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @place_action_id, 1, 'minecraft:carrots', 0.01, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @place_action_id, 1, 'minecraft:potatoes', 0.01, 1);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @break_action_id, 1, 'minecraft:melon', 0.25, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @break_action_id, 1, 'minecraft:pumpkin', 0.25, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @break_action_id, 1, 'minecraft:wheat', 0.25, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @break_action_id, 1, 'minecraft:carrots', 0.25, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @break_action_id, 1, 'minecraft:beetroots', 0.25, 5);

INSERT IGNORE INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (@farmer_job_id, @break_action_id, 1, 'minecraft:potatoes', 0.25, 5);