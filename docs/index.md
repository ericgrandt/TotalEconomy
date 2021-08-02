# Installation

1. Download [Total Economy](https://ore.spongepowered.org/Erigitic/Total-Economy/versions)
2. Put the Total Economy jar file into the mods folder
3. Start the server in order to initialize the configuration files
    - An exception will be shown in the console since the database connection information is not set yet
4. Add your database connection information to the Total Economy config file:
    - Replace `mysql` with your preferred database. Check the [Storage](https://totaleconomy.readthedocs.io/en/terewritten/storage/database/) page to view supported databases.
    - Replace `DATABASENAME` with the name of your database
    - Set username and password
5. If the database name you specified in the connection string does not exist, you'll have to create it first
6. Start the server

> ## Note
>
> The following exception is expected when initializing Total Economy for the first time: `HikariPool-1 - Exception during pool initialization`. If you continue to receive this exception after adding your database connection information to the configuration file, make sure to double-check it and verify that it's correct.
>
> A good way to check if the information is correct is to attempt to connect to your database from another tool, such as [DBeaver](https://dbeaver.io/).