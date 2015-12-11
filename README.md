HomeSpawn
=========
Spigot: https://www.spigotmc.org/resources/homespawn.14108/

Travis: https://travis-ci.org/Dart2112/HomeSpawn

This plugin allows player to teleport to the home(s) and spawn locations set using the commands below. Here is a list of current features:

•	Multiple homes

•	Admin and VIP home limits

•	List of homes with /homeslist (set InventoryMenu to true in the config to use a GUI)

•	Book of commands that is given to new players(Doesn’t contain VIP or Admin commands, set CommandBook to true in the config to enable)

•	Configurable teleport timer, players have to wait a set amount of time (without being hit by a player or a player arrow or dog) before they will be teleported. Includes a permission to bypass the delay(Automatically given to admins)

•	New player spawn, you can set where new players will spawn by using “/setspawn new”

•	Reload command for reloading all player and data configurations

•	All configuration files are loaded at plugin start and therefore will not use your precious disk time to load and save files every time they are used. This also makes loading and using data from configuration files faster, meaning that players will still be able to teleport easily and quickly even if your server is lagging.

•	Permission based help, the “/homespawn help” command will only show you what you need to see. It won’t show normal players VIP commands and it won’t show admin commands to anyone but admins.

•	Full UUID Support! This means that players can change their Mojang usernames without losing all of their home data

•	Home password system (For offline mode servers), players can set a password so that if they change their username (on offline mode this will change their UUID) they can simply run a command with the password they set with the old username and all data will be transferred over. All passwords are stored with a Salt and Hash method meaning that your passwords are completely safe, even I can’t find out what they are!
