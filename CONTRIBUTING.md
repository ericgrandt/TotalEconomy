# Contributing to Total Economy
Thank you for your interest in contributing to Total Economy. There are a few guidelines that we'd like contributors to follow before contributing.

## Contributing Issues or Feature Suggestions
When contributing issues or feature suggestions, please keep the below in mind:

+ Issues:
    + Be as descriptive as possible in both the title and description
    + Describe how to reproduce the issue
    + Include a complete error log if applicable
    + Include pictures if possible

+ Feature Suggestions:
    + Describe the feature that you'd like to see
    + Why should the feature be added?
    + How should the feature work?

+ Be sure to check back in on the issue/feature suggestion as we may need more information from you.

## Contributing Code
We are always looking for talented people to help continue making Total Economy great! If you are interested in contributing code, we ask that you follow a few guidelines.

### Getting Started

+ Find an issue with the `help wanted` label that you'd like to work on.
+ Create a branch off the branch you are targeting, most of the time `develop`, and name it `issue/[ISSUE#]-short-description-of-issue`. The short description should be no more than 4 words long.

### Code Style

**Indentation**
+ Use spaces for indentation

**File Header**
+ The top of new files must contain the license, which can be found at the top of [TotalEconomy.java](https://github.com/Erigitic/TotalEconomy/blob/develop/src/main/java/com/erigitic/main/TotalEconomy.java#L1-L24)

**Javadoc**
+ Javadoc should be present for every class and function, except for self-explanatory functions
+ The summary should accurately describe the class, function, etc.
+ Do not use @author
+ The first letter of a block tags (`@param`, `@return`, etc.) description must be capitalized

**Variables**
+ Constants should be all uppercase letters with any words separated by underscores
+ One variable per line, even if they're the same type

**Annotations**
+ Annotations should appear after the documentation and on their own line

Besides the above, try to follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html), and the style of the rest of the code. Don't worry too much about getting this exact, we'll be sure to point out any style issues if there are any.

### Submitting Pull Requests
When you are ready to submit your code for review, please do the following:

+ Ensure your branch is up to date with the branch you are targeting. Most of the time this will be `develop`.
+ Be sure to update the changelog with a short summary of your changes
+ Create a descriptive title
+ Describe what the pull request fixes, changes, or adds
+ Include the issue number that this pull request fixes (Use `Fixes`, `Closes`, etc)

## Contributing Translations

If you're interested in translating Total Economy, you can get started by following the below steps:

+ Create a new issue to let us know you are interested in translating Total Economy to your language
+ Clone [Total Economy](https://github.com/Erigitic/TotalEconomy#getting-and-building-total-economy) and create a new branch off `develop` named `issue/[ISSUE#]-[language]-translation`
+ Create a copy of the `messages_en.conf` file and rename it to `messages_[locale].conf`. A list of locales can be found here: [Locales](http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html).
+ Translate the messages to your language
+ Test the translations in game to make sure they display properly. Be sure to change the locale in the main configuration file before testing.
+ [Submit a pull request](https://github.com/Erigitic/TotalEconomy/blob/develop/CONTRIBUTING.md#submitting-pull-requests)

---

**If you have any questions, please feel free to ask in the [Total Economy Gitter](https://gitter.im/TotalEconomy/TotalEconomy).**
