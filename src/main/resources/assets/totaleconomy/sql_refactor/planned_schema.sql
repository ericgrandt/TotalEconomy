# # # EXAMPLE DATABASE SCHEMA # # #
# This file contains the currently proposed schema for the database storage.
# Everything is subject to change
# This file is set to be deleted when the migration process has been merged into the feature branch.
# # # # # # # # # # # # # # # # # #

use totaleconomy;

# Disable foreign key checks for the DROP operations
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS meta;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS currencies;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS balances;
DROP TABLE IF EXISTS jobs_experience;
SET FOREIGN_KEY_CHECKS = 1;

# # META TABLE # #
# Meta table for schema version and other generic information
# #
CREATE TABLE meta (
  ident VARCHAR(80) UNIQUE PRIMARY KEY,
  value VARCHAR(120)
) COMMENT 'Meta table for generic information such a schema version';

# # Accounts table # #
# Basic account information. UUID (might be player-uuid) and (if virtual) display name.
# #
CREATE TABLE accounts (
  uuid         CHAR(36)     UNIQUE PRIMARY KEY,
  display_name VARCHAR(60)  UNIQUE,

  INDEX (display_name)
) COMMENT 'TotalEconomy accounts table';

# # Currencies table # #
# Reference table for currencies.
# Is to be updated on each start of TE.
# It is desirable to fully populate this table to a full configuration table later on.
# Its current use is for foreign key consistency.
# #
CREATE TABLE currencies (
  ident VARCHAR(60) UNIQUE PRIMARY KEY
) COMMENT 'TotalEconomy currencies table. Currently only for constraints.';


# # Jobs table # #
# Reference table for jobs.
# Is to be updated on each start of TE.
# It is desirable to fully populate this table to a full configuration table later on.
# Its current use is for foreign key consistency.
# #
CREATE TABLE jobs (
  ident         CHAR(36)    UNIQUE PRIMARY KEY
) COMMENT 'TotalEconomy jobs table. Currently only for constraints.';

# # Balances table # #
# Balance by account uuid and currency id.
# #
CREATE TABLE balances (
  id             INT(64) UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
  account_uuid   CHAR(36)         NOT NULL,
  currency_ident VARCHAR(60)      NOT NULL,
  balance        DOUBLE           DEFAULT 0,

  FOREIGN KEY (account_uuid) REFERENCES accounts(uuid) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (currency_ident) REFERENCES currencies(ident) ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE (account_uuid, currency_ident),
  INDEX (account_uuid),
  INDEX (currency_ident)
) COMMENT 'TotalEconomy balances table.';

# # Job experience table # #
# Tracks players experience and level within jobs.
# #
CREATE TABLE jobs_experience (
  id           INT(64) UNSIGNED AUTO_INCREMENT NOT NULL PRIMARY KEY,
  account_uuid CHAR(36) NOT NULL,
  job_ident     CHAR(36) NOT NULL,
  experience   INT(32)  DEFAULT 0,
  level        INT(32)  DEFAULT 0,

  FOREIGN KEY (account_uuid) REFERENCES accounts(uuid) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (job_ident) REFERENCES jobs(ident) ON DELETE CASCADE ON UPDATE CASCADE,
  UNIQUE (account_uuid, job_ident),
  INDEX (account_uuid),
  INDEX (job_ident)
) COMMENT 'TotalEconomy job experience + levels table';

# # # DEFAULT VALUES # # #
INSERT INTO meta (`ident`, `value`) VALUES ('schema_version', '1');