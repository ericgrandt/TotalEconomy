# Contributing to Total Economy
Thanks for your interest in contributing to Total Economy. There are a few guidelines that I would like contributors to follow to make the process as easy as possible.

### Contributing Issues
* Be as descriptive as possible
* Include a complete error log if applicable
* Include pictures if possible

### Contributing Code
* Fork the repository
* Create a new branch from ‘develop'. Use slash notation (e.g. issue/issue#-short-issue-summary, issue/137-nfe-on-get-balance)
* Follow the same format/style as the rest of the code
* Add comments for new functions:
``` java
/**
 * Description of the function
 *
 * @param firstArg description of argument
 * @param secondArg description of argument
 * @return int player's balance
 */
```
* Test the change/addition and make sure nothing was accidentally broken
* Make sure your commit message clearly describes the change/addition and includes the issue number if one exists
* Submit a pull request

### Git Commit Message
* Keep length of first line to 72 characters or less
* Use present tense (e.g. “Update” instead of “Updated”)
* Include any new commands/permissions/nodes/etc. and a description for each for easy addition to documentation. New line for each one.
