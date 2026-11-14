---
layout: default
title: Installation
nav_order: 2
---

# Installation

## Requirements

| Requirement | Version                                                        |
|-------------|----------------------------------------------------------------|
| Server      | [Paper](https://papermc.io/) 26.2 or newer                     |
| Java        | 25 or newer                                                    |
| Database    | MySQL 8.0 or newer                                             |

## Steps

1. Download the latest `TotalEconomyPaper-x.y.z.jar` from
   [Modrinth](https://modrinth.com/plugin/total-economy/versions).
2. Put the JAR in your server's `plugins/` folder. If you use Vault, put Vault there too.
3. Create an empty MySQL database for Total Economy, for example `totaleconomy`. The plugin creates its own tables.
4. Start the server once. This generates `plugins/TotalEconomy/config.yml`. Total Economy can't connect to the database
   yet, so it disables itself; that's expected.
5. Stop the server and edit `plugins/TotalEconomy/config.yml`:
   - Set `database.url`, `database.user` and `database.password` for your MySQL database. The URL looks like
     `jdbc:mysql://[host]:[port]/[database_name]`.
   - Set up your default currency. See [Configuration](configuration.md) for each option.
6. Start the server again.

{: .important }
Set up the default currency **before** the second start. It's saved to the database the first time the plugin connects,
and later changes to `defaultCurrency` in `config.yml` aren't applied.

## Checking it works

Join the server and run `/balance`. You should see the starting balance of your default currency.
