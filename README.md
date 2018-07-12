HomeSpawn
=========
Spigot: https://www.spigotmc.org/resources/14108/

Travis: https://travis-ci.org/Dart2112/HomeSpawn

This plugin allows player to teleport to the home(s) and spawn locations set using the commands listed on the spigot page. Here is a list of current features:

•	Multiple homes with tab completion

•	Home renaming with /renamehome

•	Custom permissions system, add as many permissions as you want with the settings you want

•   List of homes with /homeslist (set InventoryMenu to true in the config to use a GUI)

•	Book of commands that is given to new players (Customize via a config file)

•	Configurable teleport timer, players have to wait a set amount of time (without being hit by a player or a player arrow or dog) before they will be teleported. the time to wait can be set for each permission

•	New player spawn, you can set where new players will spawn by using “/setspawn new”

•	Reload command for reloading all player and data configurations

•	Currently used configurations are cached in memory to save you ram and disk time to load files every time they are needed

•	Permission based help, the “/homespawn help” command will only show you what you need to see. It won’t show admin commands to anyone but admins.

•	Full UUID Support! This means that players can change their Mojang username without losing all of their home data

•	Home password system (For offline mode servers), players can set a password so that if they change their username (in offline mode this will change their UUID) they can simply run a command with the password they set using the old username and all data will be transferred over. All passwords are stored with a Salt and Hash method meaning that your passwords are completely safe, even I can’t find out what they are!