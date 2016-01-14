# TotalEconomy
All in one economy plugin for Minecraft and Sponge.

-

##Commands
```
/pay [player] [amount] - Pay another player
/balance - Display your balance (/bal)
/job - Display information about your current job as well as a job list
/job set [jobName] - Set your job
/job toggle - Toggle job reward notifications on/off
/setbalance [player] [amount] - Set a player's balance
/adminpay [player] [amount] - Pay a player without having money removed from your balance
/viewbalance [player] - View the balance of another player (/vbal)
```

##Permissions
```
totaleconomy.command.pay
totaleconomy.command.balance
totaleconomy.command.jobset
totaleconomy.command.jobtoggle
totaleconomy.command.job
totaleconomy.command.setbalance
totaleconomy.command.adminpay
totaleconomy.command.viewbalance
```

## Using Total Economy in your plugin
Using Total Economy in your plugin will allow you to utilize Total Economy's Account Manager within your own plugin.

* Add TotalEconomy.jar as a library in your project.
* Your @Plugin at the top of your main file should include: dependencies = "required-after:TotalEconomy". For example:

 ```java
  @Plugin(id = "APITest", name = "API Test", version = "1.0.0", dependencies = "required-after:TotalEconomy")
 ```

* In your main class add this code:

 ```java
 //THIS GOES AT THE TOP OF YOUR FILE
 private TEService service;
 
 //THIS GOES IN YOUR POSTINITIALIZATIONEVENT
 service = game.getServiceManager().provide(TEService.class).get();
 ```
 
 NOTE: You should do a check in your code to make sure that TotalEconomy.jar is present in the mods folder before running the  code.
 
 #### Functions
 ```
 void createAccount(UUID uuid) - Creates an account for the specified UUID.
 
 boolean hasAccount(UUID uuid) - Checks if the specified UUID has an account associated with it.
 
 void addToBalance(UUID uuid, BigDecimal amount) - Add the specified amount to a player's balance.
 
 void removeFromBalance(UUID uuid, BigDecimal amount) - Remove the specified amount from a player's balance.
 
 void setBalance(UUID uuid, BigDecimal amount) - Set the balance for the player.
 
 boolean hasMoney(UUID uuid, BigDecimal amount) - Checks if a player has the specified amount of money in their balance.
 
 BigDecimal getBalance(UUID uuid) - Get a player's balance.
 ```
