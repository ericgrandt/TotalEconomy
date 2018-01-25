<sub>[back](sql_proposal.md)</sub>

## Table schema

The new database table scheme is constructed using the following queries:

##### Accounts

```SQL
CREATE TABLE IF NOT EXISTS `accounts` (
	uid         VARCHAR(60)  NOT NULL PRIMARY KEY,
	displayname VARCHAR(60)  DEFAULT NULL,
	job         VARCHAR(60)  DEFAULT NULL
) COMMENT='Main accounts table';
```

##### Account options

```SQL
CREATE TABLE IF NOT EXISTS `accounts_options` (
	id    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uid   VARCHAR(60)  NOT NULL,
    ident VARCHAR(60)  NOT NULL,
    value VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (uid) REFERENCES accounts(uid) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT UNIQUE (uid, ident)
) COMMENT='User account options';
```

##### Currencies[^1]

```sql
CREATE TABLE IF NOT EXISTS `currencies` (
	currency VARCHAR(60) NOT NULL PRIMARY KEY
) COMMENT='DATABASE REFERENCE | All currencies | Automatically updated on restart';
```

##### Balances

```SQL
CREATE TABLE IF NOT EXISTS `balances` (
	id       INT UNSIGNED  AUTO_INCREMENT PRIMARY KEY,
	uid      VARCHAR(60)   NOT NULL,
    currency VARCHAR(60)   NOT NULL,
    balance  DECIMAL(19,2) DEFAULT 0,
    FOREIGN KEY (uid) REFERENCES accounts(uid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (currency) REFERENCES currencies(currency) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UNIQUE (uid, currency)
) COMMENT='All balances by account and currency';
```

##### Jobs[^1] 

```SQL
CREATE TABLE IF NOT EXISTS `jobs` (
	uid VARCHAR(60) NOT NULL PRIMARY KEY
) COMMENT='DATABASE REFERENCE | All jobs | Automatically updated on restart'; 
```

##### Job progress

```sql
CREATE TABLE IF NOT EXISTS `jobs_progress` (
	id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uid        VARCHAR(60)  NOT NULL,
    job        VARCHAR(60)  NOT NULL,
    level      INT UNSIGNED DEFAULT 0,
    experience INT UNSIGNED DEFAULT 0,
    FOREIGN KEY (uid) REFERENCES accounts(uid) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (job) REFERENCES jobs(uid) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UNIQUE (uid, job)
) COMMENT='The job progress table';
```



I think these tables schemas are pretty much self-explanatory so I won't go into further detail here. The "spelling errors" in the names are to allow grouping in web backends like PHPMyAdmin.

---

[^1]: These tables are updated upon initialization. They are purely for constraints in other tables.