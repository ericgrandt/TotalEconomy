<sub>[back](sql_proposal.md)</sub>

## The legacy migrator

The legacy migrator migrates the previous database schema to the newer - more migration friendly - one.

1. The migrator firstly moves the old tables out of the way to prevent name conflicts. (They're just prefixed with "migr_").
2. The new tables are created.
3. The currencies and jobs are inserted.
4. The player accounts are migrated.
5. The virtual accounts are migrated.
6. All balances are migrated.
7. Jobs progress is migrated.
8. TE_META will be initialized with ``schema_version`` 1

The old tables will __NOT__ be automatically deleted. This is to save the possibility to manually correct any import errors.

Please note: When the migration was erroneous it will only re-trigger when the created tables are dropped and the renames have been reverted. 