package net.lapismc.HomeSpawn.api;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.HomeSpawnComponents;
import net.lapismc.HomeSpawn.PasswordHash;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

/**
 * api Class to get Config Files
 *
 * @author Dart2112
 */
public class Configs {

    private HomeSpawn plugin;
    private ArrayList<Plugin> blocked = new ArrayList<>();

    public Configs(Plugin plugin) {
        if (plugin.getName().equalsIgnoreCase("HomeSpawn")) {
            return;
        }
        HomeSpawnComponents hsc = new HomeSpawnComponents();
        if (hsc.api()) {
            this.plugin.logger.info("Plugin " + plugin.getName()
                    + " has connected to the API");
        } else {
            this.plugin.logger.severe("Plugin "
                    + plugin.getName() + " has attempted to connect to the HomeSpawn API," +
                    " But as it is disabled the plugin was denied access");
            blocked.add(plugin);
        }
    }

    public void init(HomeSpawn p) {
        this.plugin = p;
    }

    /**
     * Reloads all configs from disk
     *
     * @throws IOException
     */
    public void reloadConfigs(Plugin p) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        plugin.HSConfig.reload("Silent");
    }

    /**
     * Returns the currently loaded Spawn.yml
     */
    public YamlConfiguration getSpawnConfig(Plugin p) {
        if (blocked.contains(p)) {
            return null;
        }
        return plugin.HSConfig.spawn;
    }

    /**
     * Saves the given YamlConfiguration as the Spawn.yml file
     *
     * @throws IOException
     */
    public void saveSpawnConfig(Plugin p, YamlConfiguration SpawnConfig) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        plugin.HSConfig.spawn = SpawnConfig;
        plugin.HSConfig.spawn.save(plugin.HSConfig.spawnFile);
        plugin.HSConfig.reload("Silent");
    }

    /**
     * Returns the currently loaded Messages.yml
     */
    public YamlConfiguration getMessagesConfig(Plugin p) {
        if (blocked.contains(p)) {
            return null;
        }
        return plugin.HSConfig.messages;
    }

    /**
     * Saves the given YamlConfiguration as the Messages.yml file
     *
     * @throws IOException
     */
    public void saveMessagesConfig(Plugin p, YamlConfiguration MessagesConfig) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        plugin.HSConfig.messages = MessagesConfig;
        plugin.HSConfig.messages.save(plugin.HSConfig.messagesFile);
        plugin.HSConfig.reload("Silent");
    }

    /**
     * Returns the currently loaded Passwords.yml
     */
    public YamlConfiguration getPasswords(Plugin p) {
        if (blocked.contains(p)) {
            return null;
        }
        return plugin.HSConfig.passwords;
    }

    /**
     * Saves the given YamlConfiguration as the Passwords.yml file
     *
     * @throws IOException
     */
    public void savePasswords(Plugin p, YamlConfiguration Passwords) throws IOException {
        if (blocked.contains(p)) {
            return;
        }
        plugin.HSConfig.passwords = Passwords;
        plugin.HSConfig.passwords.save(plugin.HSConfig.passwordsFile);
        plugin.HSConfig.reload("Silent");
    }

    /**
     * Checks if the given string matches the password on file
     *
     * @throws NoSuchAlgorithmException and InvalidKeySpecException
     */
    public boolean checkPassword(Plugin p, String playerName, String Password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (blocked.contains(p)) {
            return false;
        }
        String hash = getPasswords(p).getString(playerName);
        return PasswordHash.validatePassword(Password, hash);
    }

    /**
     * Hashes the password and saves it to as given username then saves the passwords config
     *
     * @throws IOException, NoSuchAlgorithmException and InvalidKeySpecException
     */
    public void setPassword(Plugin p, String playerName, String rawPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (blocked.contains(p)) {
            return;
        }
        String hash = PasswordHash.createHash(rawPassword);
        YamlConfiguration passwords = getPasswords(p);
        passwords.set(playerName, hash);
        savePasswords(p, passwords);
    }

}
