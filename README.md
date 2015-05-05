# TotalEconomy
All in one economy plugin for Minecraft and Sponge.

-

## Using Total Economy in your plugin
* Add TotalEconomy.jar as a library in your project.
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
 boolean hasAccount(Player player) - Checks if the specified player has an account.
 
 void addToBalance(Player player, BigDecimal amount) - Add the specified amount to a player's balance.
 
 void removeFromBalance(Player player, BigDecimal amount) - Remove the specified amount from a player's balance.
 
 boolean hasMoney(Player player, BigDecimal amount) - Checks if a player has the specified amount of money in their balance.
 
 BigDecimal getBalance(Player player) - Get the passed in player's balance.
 ```
