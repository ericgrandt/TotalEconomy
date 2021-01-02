# Testing

Total Economy has multiple unit and integration tests to help ensure everything is functioning properly between releases. Every piece of code that gets added should be covered by unit tests, and if applicable, integration tests as well. There are some cases where this may not be worth the trouble. Each test, or test class, is tagged as either `@Tag('Unit')` or `@Tag('Integration')` in order to allow running only certain types of tests.

## Running Unit Tests

Unit tests don't require any extra setup to get running. Simply run `./gradlew test` to run all of the unit tests.

## Running Integration Tests

Integration tests require some extra setup before they can be run as they require a MySQL database. It is recommended to point the connection string to a database intended for testing as the tables get truncated before each integration test is run.

1. Set the `connectionString`, `user`, and `password` in `TestUtils.java`
2. Prepare the database by running the scripts in `src/main/resources/assets/totaleconomy/schema/mysql.sql`
3. Run `./gradlew integrationTest`

If you'd like to run every test at once, run `./gradlew fullTest`.