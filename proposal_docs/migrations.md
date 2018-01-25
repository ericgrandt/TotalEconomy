<sub>[back](./SQL Proposal.md)</sub>

## Migrations (SQL)

With the proposal TotalEconomy will automatically do migrations when necessary.

This is supposed to allow changes in the table schemas for future updates without forcing system administrators to worry about that. Unless the import runs into exceptions of course.

### The meta-table

With the proposal TE will create a table named ``te_meta`` which will have the following schema:

```sql
CREATE TABLE IF NOT EXISTS `te_meta` (
	ident VARCHAR(60) NOT NULL PRIMARY KEY,
    value VARCHAR(60) DEFAULT NULL
) COMMENT='Table for versioning an other internal stuff';
```

This table will be used by the ``SqlManager`` for determining the schema version. (``ident`` will be ``"schema_version"`` and the value will be a serialized Integer)

If this table does not exist the ``SqlManager`` will additionally check if the totaleconomy database is empty.

Depending on that result the ``SqlManager`` determines if a migration has to be started.

### SQL migrators[^1] 

In order to perform migration implementations of the ``SqlMigrator`` and an instance of the ``MigrationManager`` are used.

Currently the ``MigrationManager`` just takes an Integer from the ``SqlManager`` to determine which ``SqlMigrator`` to use but I can think of more sophisticated (though more complicated) ways of implementation. (I did not want to implement one of those right away as the changes are widespread already)

```java
// Add new migrators here
// This is to ensure a successful migration even when administrators skip versions.
// For greater version jumps the migration may need to be re-run several times.
switch (migrateFrom) {
	case -1: migrator = new FlatFileMigrator(); break;
    case 0: migrator = new LegacySchemaMigrator(); break;
    default: throw new RuntimeException("No migrator for: " + migrateFrom);
}
```

As can be seen the migration also includes converting data from the configuration to the database when the database feature has been enabled on an empty database.

There's also a ``SqlMigrator`` for converting the old database format to the one proposed by this PR.

Further: Future updates which change the database schema could add migrators here.

I'm not going into what the migrators do in detail as the process should be fairly straight forward: Take old data => Insert in new order into new table.

[^1]: Other migrations are referenced in [ConfigChanges](./ConfigChanges.md)

### Jumping versions

While one may think that removing outdated migrators with new versions is a good idea to reduce unnecessary code within TE there's a catch: When this is __not__ done it'll automatically allow administrators to perform jumps in schema versions that take more than just one migration without needing to download the necessary versions of TotalEconomy (or even Sponge) as they can just load up TE until the old schema has been migrated all the way to the new version. (I may add code to the proposal which could do this automatically when Eric approves of this idea)

### Exceptions / Logs

All migrators collect non-critical exceptions as ``MigrationException``s in a local list that will be dumped into a log file when the process has finished.

The file is located in "config/totaleconomy/migration.log"

---

#### Neat-to-know

For restrictive server administrators who prefer to either do migrations by hand or using their own tools the mechanism can be completely disabled.

The ``SqlManager`` reads the system property ``totaleconomy.skipDBMigrateCheck`` and skips the migration process when its value reads ``true``. (Plain string)

TE will just log a warning for support purposes in that case.

___Important!___ _Please be aware the plugin will assume the database schema to be (at least) compatible to the schema version the build is written for!_