v1.8.1

+ CHANGE: Baltop command now has database support
+ FIX: Keys are now properly registered which fixes a crashing problem
+ FIX: Configuration files are now saved when the server stops if the save interval is greater then 0
+ FIX: Only the shop owner can stock their shop now

v1.8.0

+ ADDED: Chest shops
+ ADDED: Ability to set id traits, such as block variants and types, for blocks in job sets
+ CHANGE: Rework job rewards
+ CHANGE: Change job sets file name from `jobSets` to `jobsets`
+ CHANGE: The `jobs` and `jobsets` configuration files are now copied from the resources folder instead of being created programmatically
+ CHANGE: Change the color and formatting of job signs
+ CHANGE: Experience points are no longer reset to 0 on level up
+ UPDATE: Updated to API 7

v1.7.0

+ Multi-Currency support
+ Messages can now be changed in the messages_en configuration file
+ CHANGE: A custom database name can now be set instead of being forced to use `totaleconomy` as the database
+ FIX: Account configuration file now properly saves when the server is stopped
+ FIX: Number Format Exception no longer occurs when setting a balance to a large number

v1.6.0

+ SQL Support
+ Reusable job sets that allow for easier job creation
+ Add option to change location of currency symbol (eg. $5 or 5$)
+ Add `growthTrait` node to `break` nodes to support crops (see the farmer job)
+ Add `farmer` to the default jobs
+ Add `/job set <job> <player>` command to set another players job
+ Virtual account support
+ CHANGE: Updated format of job info command
+ CHANGE: Commands now properly return as successful or unsuccessful
+ CHANGE: Baltop command now only shows a maximum of 10 balances
+ CHANGE: Added "/money" alias to balance command
+ CHANGE: Pay command no longer works from command blocks or console (Use adminpay)
+ CHANGE: In case of duplicate rewards in a job (across multiple sets) the one giving more exp is chosen rather than the last match
+ CHANGE: No gains from breaking a block placed by any player UNLESS the block is indicating to have a `growTrait`

v1.5.3

+ FIX: NullPointerException no longer occurs when reloading configuration files and not having jobs enabled

v1.5.2

+ CHANGE: Changed output for when an error occurs during the reloading of configuration files
+ FIX: Baltop command now sorts balances in descending order

v1.5.1

+ CHANGE: Accounts configuration file now saves while the server is in the process of stopping
+ CHANGE: Changed the way display names are retrieved
+ CHANGE: Changed the way block ids are handled
+ CHANGE: Admin pay command can now be used to remove money (/adminpay [PLAYER] -[VALUE])
+ FIX: EconomyTransactionEvent no longer encounters a cast exception
+ FIX: Kills with bows are now properly handled

v 1.5.0

+ Balance Top command (/balancetop, /baltop)
+ Added alias for the set balance command (/setbal)
+ Added a money cap that can be enabled/disabled as well as set from the main config
+ Ability to set default job notifications state. Thanks to @MarkL4YG.
+ Jobs config can now be reloaded without having to restart the server. (/sponge plugins reload)
+ The pay command can be used to add/remove money from the console. To remove money just do "pay [NAME] -[AMOUNT]".
+ Job Info command (/job info) (Displays a paginated list of everything that earns exp/money for the players current job in a paginated list)
+ Updated to latest versions of Forge and Sponge
+ CHANGE: EXP/Money is no longer rewarded for breaking blocks that a player placed. This replaces the preventJobFarming node.
+ FIX: Job permissions were changed back to their normal values
+ FIX: Issue with Cause.of has been fixed

v 1.4.0

+ Updated API version

v 1.3.0

+ Updated to use the Economy API and to work with the latest version of Sponge

v 1.2.3

+ Updated to latest version of Sponge (Command fixed)
+ View Balance command (/viewbalance [PLAYER]) (/vbal [PLAYER])

v 1.2.2

+ Updated to latest version of Sponge

V 1.2.1

+ Updated to latest version of Sponge
+ Alias for balance command (/bal)

V1.2.0

+ Warrior Job
+ Fisherman Job
+ Job set command is no longer case sensitive
+ Change job from signs
+ BUG FIX: IOException when loading configs for first time has been fixed

V1.1.3

+ Updated to the latest version of the Sponge API

V1.1.2

+ Updated to the latest version of Sponge

V1.1.1

+ Updated to the latest version of Sponge

v1.1.0

+ Bug Fix: Negative numbers can no longer be passed into pay commands.
+ Bug Fix: Pay commands now check for anything but numbers and a single decimal in the amount in order to prevent errors.
+ Admin Pay Command(/adminpay [player] [amount])
+ Job Permissions
+ Ability to enable/disable salaries for individual jobs.

v1.0.9

+ Job salaries that are, by default, paid out every 5 minutes. This time can be changed from the jobs.conf file.
+ Starting balance for new players can now be changed from the totaleconomy.conf file.
