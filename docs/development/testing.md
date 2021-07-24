# Testing

Total Economy has multiple unit and integration tests to help ensure everything is functioning properly between releases. Every piece of code that gets added should be covered by unit tests, and if applicable, integration tests as well. There are some cases where this may not be worth the trouble. Each test, or test class, is tagged as either `@Tag('Unit')` or `@Tag('Integration')` in order to allow running only certain types of tests.

## Running Tests

Tests don't require any extra setup to get running. Simply run `./gradlew test` to run all of them, unit and integration.