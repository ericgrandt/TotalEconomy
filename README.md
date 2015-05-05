# TotalEconomy
All in one economy plugin for Minecraft and Sponge.

-

## Using Total Economy in your plugin
* Add TotalEconomy.jar as a library in your project.
* In your PostInitializationEvent add this code:

 ```
 //THIS GOES AT THE TOP OF YOUR FILE
 private TEService service;
 
 //THIS GOES IN YOUR POSTINITIALIZATIONEVENT
 service = game.getServiceManager().provide(TEService.class).get();
 ```
 
 NOTE: You should do a check in your code to make sure that TotalEconomy.jar is present in the mods folder before running the code.
