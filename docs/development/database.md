The Total Economy database schema can be found in the resources directory in the `schema.sql` file. Extra documentation on the tables and some of their columns can be found below.

## Tables

### te_account

Stores a player's Minecraft UUID and the date that user was created in the database. In most cases, the created date is equal to the first time they joined the server. Other tables reference `te_account` in order to link data to a certain player (e.g. `te_balance`).

### te_virtual_account

Similar to `te_account` except for non-player accounts.

### te_currency

Stores each currency and is referenced by a few tables (e.g. `te_default_balance` and `te_balance`). The `num_fraction_digits` can be set to any valid integer value, but it will be limited to the range set by the `balance` column in the `te_balance` table when stored.

For `is_default`, there is no constraint on this column meaning multiple defaults could be set, though it isn't recommended. Multiple defaults will most likely lead to a random currency being chosen as the default each time it gets queried.

### te_default_balance

Each currency can be setup to have a default balance that is used when a player's balances are initially created; this table stores those default balances. The `default_balance` is stored with 2 fractional digits, but when being displayed in game, it will take into account the value of `num_fraction_digits` on the currency.

### te_balance

The player's balances for each currency are stored in this table. Just like `te_default_balance`, the `balance` column is stored with 2 fractional digits but will display in-game with the number of fractional digits for the respective currency.