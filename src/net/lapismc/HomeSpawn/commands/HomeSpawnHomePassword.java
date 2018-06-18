/*
 * Copyright 2018 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapismc.HomeSpawn.commands;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.util.LapisCommand;
import net.lapismc.HomeSpawn.util.PasswordHash;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class HomeSpawnHomePassword extends LapisCommand {

    private final HomeSpawn plugin;

    public HomeSpawnHomePassword(HomeSpawn p) {
        super("homepassword", "Allows the player to set a password and transfer homes to a new username on an offline mode server", new ArrayList<>());
        this.plugin = p;
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.HSConfig.getMessage("Error.MustBePlayer"));
            return;
        }
        Player p = (Player) sender;
        File file = new File(this.plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator
                + p.getUniqueId() + ".yml");
        FileConfiguration getHomes = YamlConfiguration
                .loadConfiguration(file);
        File file3 = new File(this.plugin.getDataFolder() + File.separator
                + "PlayerData" + File.separator + "Passwords.yml");
        FileConfiguration getPasswords = YamlConfiguration
                .loadConfiguration(file3);
        if (!this.plugin.getServer().getOnlineMode()) {
            if (args.length <= 1) {
                PassHelp(p);
            } else if (args.length == 3) {
                String string = args[0];
                if (string.equalsIgnoreCase("set")) {
                    if (args[1].equals(args[2])) {
                        String pass = args[1];
                        String passHash;
                        try {
                            passHash = PasswordHash.createHash(pass);
                        } catch (NoSuchAlgorithmException
                                | InvalidKeySpecException e1) {
                            e1.printStackTrace();
                            p.sendMessage(plugin.HSConfig.getColoredMessage("Password.SaveFailed"));
                            return;
                        }
                        getPasswords.set(p.getName(), passHash);
                        p.sendMessage(plugin.HSConfig.getColoredMessage("Password.SetTo"));
                        p.sendMessage(plugin.SecondaryColor + pass);
                        try {
                            getPasswords.save(file3);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.sendMessage(plugin.HSConfig.getColoredMessage("Password.NoMatch"));
                    }
                } else if (string.equalsIgnoreCase("transfer")) {
                    String pass = args[2];
                    String name = args[1];
                    File namefile = new File(this.plugin.getDataFolder()
                            .getAbsolutePath()
                            + File.separator
                            + "PlayerData"
                            + File.separator
                            + "PlayerNames"
                            + File.separator
                            + name
                            + ".yml");
                    FileConfiguration getOldName = YamlConfiguration
                            .loadConfiguration(namefile);
                    boolean Password = false;
                    try {
                        Password = PasswordHash.validatePassword(pass,
                                getPasswords.getString(name));
                    } catch (NoSuchAlgorithmException
                            | InvalidKeySpecException e2) {
                        e2.printStackTrace();
                        p.sendMessage(plugin.HSConfig.getColoredMessage("Password.CheckError"));
                    }
                    if (namefile.exists() && Password) {
                        String uuid = getOldName.getString("UUID");
                        File OldUUIDFile = new File(
                                this.plugin.getDataFolder() + File.separator
                                        + "PlayerData" + File.separator
                                        + uuid + ".yml");
                        file.delete();
                        OldUUIDFile.renameTo(file);
                        getHomes.set("Name", p.getUniqueId()
                                .toString());
                        try {
                            getHomes.save(file);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        OldUUIDFile.delete();
                        namefile.delete();
                        try {
                            getHomes.save(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        p.sendMessage("Data Transferred!");
                        getPasswords.set(p.getName(),
                                getPasswords.getString(name));
                        getPasswords.set(name, null);
                    } else {
                        p.sendMessage("That Name Or Password Was Incorrect!");
                    }
                }
            }
        } else {
            p.sendMessage("This Command Isnt Used As This Is An Online Mode Server");
        }

    }

    private void PassHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "---------------------"
                + ChatColor.RED + "Homespawn" + ChatColor.GOLD
                + "---------------------");
        player.sendMessage(ChatColor.RED + "/homepassword help:"
                + ChatColor.GOLD + " Shows This Text");
        player.sendMessage(ChatColor.RED
                + "/homepassword set [password] [password]:" + ChatColor.GOLD
                + " Sets Your Transfer Password");
        player.sendMessage(ChatColor.RED
                + "/homepassword transfer [old username] [password]:"
                + ChatColor.GOLD
                + " Transfers Playerdata From Old Username To Current Username");
        player.sendMessage(ChatColor.GOLD
                + "-----------------------------------------------------");
    }

}
