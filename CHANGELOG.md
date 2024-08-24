# Changelog

All notable changes to Total Economy will be documented in this file.

## [0.13.1] - 2024-08-XX

### Fixed

- Fix an issue with SpongeForge not working due to duplicate dependencies

## [0.13.0] - 2024-07-20

This update causes a breaking change for custom job rewards. See the "Changed" section below for more information.

### Added

- Jobs added to Sponge version

### Changed

- Both Bukkit and Sponge use the common job implementation
    - Experience tracking through the boss bar currently not implemented; will be added in a future update
    - Level up notifications removed; will be added back in a future update or implemented in a different way
    - Full ids are now used (e.g. `minecraft:coal_ore` instead of `coal_ore`). This will cause existing custom job rewards to no longer work. Add the relevant prefix to custom job rewards to fix.

### Fixed

- Fixed a bug where a player would be rewarded experience and money when performing an action that doesn't match what's in the database (e.g. being rewarded for placing a spruce_log when it should only reward for breaking)

## [0.12.1] - 2024-04-24

### Changed

- Economy implementations for Vault and Sponge now support withdrawing and depositing an amount of zero
