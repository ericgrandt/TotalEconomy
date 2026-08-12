<div align="center">

# Total Economy

**A modular economy plugin for Minecraft.**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.md)
[![Paper](https://img.shields.io/badge/Paper-compatible-blue.svg)](https://papermc.io)
[![Vault](https://img.shields.io/badge/Vault-compatible-green.svg)](https://www.spigotmc.org/resources/vault.34315/)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/RHwfNWap)](https://modrinth.com/plugin/total-economy)
[![GitHub Stars](https://img.shields.io/github/stars/ericgrandt/TotalEconomy?style=social)](https://github.com/ericgrandt/TotalEconomy)

</div>

## Overview

Total Economy is a Minecraft plugin that provides a full-fledged economy for your server. Rather than packing every
feature into a single plugin with toggles, Total Economy takes a modular approach: a lightweight core handles the
essentials, and additional features are added as separate plugins that hook into the core API. Install only what your
server needs, and skip what it doesn't.

## Features

- **Multi-Currency Support** - Define multiple currencies with configurable starting balances
- **Player Accounts** - Automatic player account creation for each currency
- **Money Transfers** - Send and receive payments between players in any currency
- **Extensible API** - Build custom add-on plugins that extend core functionality
- **Vault Integration** - Full Vault API implementation for compatibility with many plugins

## Planned Add-Ons

Additional features are being developed as separate plugins that extend the core Total Economy plugin:

| Add-On       | Status         | Description                                                           |
|--------------|----------------|-----------------------------------------------------------------------|
| Jobs         | In-development | Assign roles to players with configurable rewards for in-game actions |
| Shops        | Planned        | Buy and sell items through player-run or server-controlled shops      |
| Web UI       | Planned        | Browser-based dashboard for managing data and viewing analytics       |

## Installation

1. Download the latest release from [Modrinth](https://modrinth.com/plugin/total-economy/versions)
2. Place the JAR file in your server's `plugins/` directory
3. Start (or restart) your server
    - This will fail on initial run; modify your config.yml to point to your database
4. Configure `plugins/TotalEconomy/config.yml` to your liking
5. Restart to apply changes

### Minimum Requirements

These are the current versions that the latest release was tested against. Older/Newer versions may still work.

| Requirement | Version      |
|-------------|--------------|
| Minecraft   | 26.1.2       |
| Java        | 25+          |
| Server      | Paper 26.1.2 |
| MySQL       | 8.0+         |

## Building from Source

**Requirements:** Java 25+, Gradle 9+

```bash
git clone https://github.com/ericgrandt/TotalEconomy.git
cd TotalEconomy
./gradlew :totaleconomy-paper:shadowJar
```

The built JAR will be in `totaleconomy-paper/build/libs/TotalEconomyPaper-{version}.jar`.

## Project Structure

```
TotalEconomy/
├── common/                   # Shared logic across all plugins 
├── totaleconomy-api/         # Public API for add-on plugins
├── totaleconomy-core/        # Shared logic for TotalEconomy
├── totaleconomy-paper/       # Paper implementation for TotalEconomy
├── totaleconomy-jobs-core/   # Shared logic for TotalEconomy-Jobs
├── totaleconomy-jobs-paper/  # Paper implementation for TotalEconomy-Jobs
└── docs/                     # Documentation site (Jekyll)
```

## Documentation

- [Documentation](https://ericgrandt.github.io/TotalEconomy/) - Guides and configuration reference
- [Issues](https://github.com/ericgrandt/TotalEconomy/issues) - Bug reports and feature requests
- [Discussions](https://github.com/ericgrandt/TotalEconomy/discussions) - Questions, ideas, and community talk

## Contributing

Contributions are welcome! Fork the repo, create a feature branch, and open a pull request. If you're planning a larger
change, consider starting a [discussion](https://github.com/ericgrandt/TotalEconomy/discussions) first so we can align
on direction.

## License

Total Economy is released under the [MIT License](LICENSE.md).
