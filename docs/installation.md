1. Download [Total Economy](https://ore.spongepowered.org/Erigitic/Total-Economy/versions)
2. Put the Total Economy jar file into the mods folder
3. Start the server
   - Total Economy will display an error message: `Unable to connect to the database`
4. Add database connection information to the config:
    - Replace `mysql` with your preferred database. Check `Storage/Database` to view supported databases.
    - Replace `DATABASENAME` with the name of your database
    - Set username and password
5. If the database name you specified in the connection string does not exist, you'll have to create it first
6. Start the server