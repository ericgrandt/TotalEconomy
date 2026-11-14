---
layout: default
title: Configuration
nav_order: 3
---

# Configuration

Total Economy is configured in `plugins/TotalEconomy/config.yml`. The file is created the first time the server starts.
Restart the server after changing it.

## Default config

```yml
database:
  url: jdbc:mysql://localhost:3306/totaleconomy
  user: root
  password: password
defaultCurrency:
  code: USD
  name: Dollar
  pluralName: Dollars
  symbol: $
  fractionalDigits: 2
  startingBalance: 100.00
```

## Database

| Option              | Description                                                                            |
|---------------------|----------------------------------------------------------------------------------------|
| `database.url`      | JDBC URL of your MySQL database, in the form `jdbc:mysql://[host]:[port]/[database_name]` |
| `database.user`     | MySQL user Total Economy connects as                                                   |
| `database.password` | Password for that user                                                                 |

The user needs permission to create tables in the database, because Total Economy creates its tables on startup.

## Default currency

The default currency is used whenever a command or plugin doesn't name a currency, for example `/balance` with no
arguments, or any plugin that goes through Vault.

| Option                             | Default   | Description                                                          |
|------------------------------------|-----------|----------------------------------------------------------------------|
| `defaultCurrency.code`             | `USD`     | Short code players type in commands, such as `/balance USD`. Up to 10 characters. |
| `defaultCurrency.name`             | `Dollar`  | Singular name. Up to 20 characters.                                  |
| `defaultCurrency.pluralName`       | `Dollars` | Plural name. Up to 21 characters.                                    |
| `defaultCurrency.symbol`           | `$`       | Symbol shown before amounts, as in `$10.00`. Up to 4 characters. Leave it empty to show the name instead, as in `10 Dollars`. |
| `defaultCurrency.fractionalDigits` | `2`       | Number of decimal places shown. Use `0` for whole-number currencies. |
| `defaultCurrency.startingBalance`  | `100.00`  | Balance a player's account starts with.                              |

{: .important }
The default currency is saved to the database the first time Total Economy connects. Changing `defaultCurrency`
afterwards has no effect, so decide on these values before you first start the plugin with a working database.
