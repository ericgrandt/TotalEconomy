---
layout: default
title: Installation
nav_order: 2
---

# Installation

## Requirements

### Bukkit

- [Spigot](https://www.spigotmc.org/)/[Paper](https://papermc.io/)
- [Vault](https://dev.bukkit.org/projects/vault)
- MySQL (>= 8) database

### Sponge (in development)

- [Sponge](https://spongepowered.org/)
- MySQL (>= 8) database

## Step-by-step

1. Move the `TotalEconomy-x.y.z.jar` into the `plugins` folder
2. Start the server to generate the configuration file
   - Optionally, create the configuration beforehand using [config.yml](https://github.com/ericgrandt/TotalEconomy/blob/master/totaleconomy-bukkit/src/main/resources/config.yml) as a reference
3. Update the configuration file with your database information
   - For the database url, follow this structure: `jdbc:mysql://[host]:[port]/[database_name]`
4. Restart the server