<sub>[back](./SQL Proposal.md)</sub>

## The FlatFileMigrator[^1] 

#### Process

The flat file migration process basically works like this.

1. Load the account.conf
2. Call the post-initialization of the ``SqlManager``
   * This inserts the currencies and jobs (for constraints!)
3. Iterate over all account nodes
   1. Import the account data (uid, displayname, job)
   2. Import the balances iterating over the ``balances`` nodes children
   3. Import the job progress iterating over the ``jobstats`` nodes children
4. Update ``te_meta`` => ``schema_version`` to ``1``

[^1]: __Note__: Both the migrator and this description assume the database tables have been created successfully! 

#### Updates

When updates change either the configuration syntax or the database schema this class has to be adapted to those changes.

Additionally when the database schema changed step 4 will need to update to the new version value.