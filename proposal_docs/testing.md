## Testing the development release

To allow testing of the proposed code to a broader spectrum a bleeding release has been created. You can find it in the [forked repository releases](https://github.com/MarkL4YG/TotalEconomy/releases).

___IMPORTANT___ Be aware that this is a bleeding-edge release! This is neither an official release nor allowed to even touch productive environments___!___ 

---

#### Kill switch

The PR intentionally includes a kill switch to prevent starting this release on productive environments accidentally as this may __break all data__.

In order to run the plugin you'll need to add an additional argument to the server startup command. Add ``-Dtotaleconomy.disable-bleeding-killswitch=true`` to your JVM arguments.

If you don't know how to do that: _Sorry, but this release is not for you. Please wait for the stable release so you can use the features without having to worry about your data._