# Hardcore Wither
The more Withers you fight, the harder the fight gets.

Withers spawned around you get increasingly difficult special abilities. If a Wither is spawned with multiple players around, all of their experience with Withers is taken into account.

There are several special abilities (boosted health) most of which are intended to be discovered the hard way. All abilities can be disabled/adjusted in configs.

There are plans for more special abilities in the future, suggestions are welcome (issues submitted to the repository are perfered).

There is also a way to get Tinkers' Construct Green Heart Canisters if that mod is present (disablable in configs).

Requires a minimum of Forge 1384.

Feel free to use in modpacks; notification is appreciated, but not required.


## Building/Working with the code

This is semi-automated, more to come. These assume the use of Eclipse

1. Clone Repo

2. Dowload Forge 11.15.1.1722 MDK from http://files.minecraftforge.net/

3. Extract "eclipse" folder fri MDK into location of cloned repo

4. In cloned repo run "./gradlew setupDecompWorkspace eclipse" or "gradlew.bat setupDecompWorkspace eclipse"

5. Open Eclipse, point workspace to path-to-repo/eclipse

6. Recommended: change project name in Eclipse to "HardcoreWither" (this caugses Eclipse to realize that it's a git repo)
