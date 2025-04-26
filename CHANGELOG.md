# Changelog

All notable changes to Total Economy will be documented in this file.

## [0.15.0] - 2025-04-25

### Fix

- DamageEntityEvent changed to DamageEntityEvent.Post to fix error

## [0.15.0] - 2025-03-20

- Updated to support Minecraft 1.21.4

## [0.14.0] - 2024-11-24

### Added

- Experience bars have been added back. On job action, the experience bar will show up for around 5 seconds before disappearing again.

### Fix

- Sponge version now properly allows changing of configuration values instead of forcing defaults

### Change

- On player join, job data is only created if jobs are enabled

## [0.13.2] - 2024-09-14

### Added

- Implementations for multi world Vault API functions (Bukkit only)
  - Since Total Economy does not support multi-world, these implementations will act the same as the global Vault API functions.

## [0.13.1] - 2024-09-08

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
