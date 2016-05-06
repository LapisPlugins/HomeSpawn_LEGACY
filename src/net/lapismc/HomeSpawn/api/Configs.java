package net.lapismc.HomeSpawn.api;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.PasswordHash;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * api Class to get Config Files
 *
 * @author Dart2112
 */
public class Configs {
    private final HomeSpawn plugin;

    public Configs(Plugin plugin) {
        //check if API is enabled
        //report to console that Plugin plugin is using the API
    }
    
    protected void init(Plugin p){
        this.plugin = p
    }

    /**
     * Reloads all configs from disk
     *
     * @throws IOException
     */
    public void reloadConfigs() throws IOException {
        plugin.reload("Silent");
    }

    /**
     * Returns the currently loaded Spawn.yml
     */
    public YamlConfiguration getSpawnConfig() {
        return plugin.spawn;
    }

    /**
     * Saves the given YamlConfiguration as the Spawn.yml file
     *
     * @throws IOException
     */
    public void saveSpawnConfig(YamlConfiguration SpawnConfig) throws IOException {
        plugin.spawn = SpawnConfig;
        plugin.spawn.save(plugin.spawnFile);
        plugin.reload("Silent");
    }

    /**
     * Returns the currently loaded Messages.yml
     */
    public YamlConfiguration getMessagesConfig() {
        return plugin.messages;
    }

    /**
     * Saves the given YamlConfiguration as the Messages.yml file
     *
     * @throws IOException
     */
    public void saveMessagesConfig(YamlConfiguration MessagesConfig) throws IOException {
        plugin.messages = MessagesConfig;
        plugin.messages.save(plugin.messagesFile);
        plugin.reload("Silent");
    }

    /**
     * Returns the currently loaded Passwords.yml
     */
    public YamlConfiguration getPasswords() {
        return plugin.passwords;
    }

    /**
     * Saves the given YamlConfiguration as the Passwords.yml file
     *
     * @throws IOException
     */
    public void savePasswords(YamlConfiguration Passwords) throws IOException {
        plugin.passwords = Passwords;
        plugin.passwords.save(plugin.passwordsFile);
        plugin.reload("Silent");
    }

    /**
     * Checks if the given string matches the password on file
     *
     * @throws NoSuchAlgorithmException and InvalidKeySpecException
     */
    public boolean checkPassword(String playerName, String Password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hash = getPasswords().getString(playerName);
        return PasswordHash.validatePassword(Password, hash);
    }

    /**
     * Hashes the password and saves it to as given username then saves the passwords config
     *
     * @throws IOException, NoSuchAlgorithmException and InvalidKeySpecException
     */
    public void setPassword(String playerName, String rawPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String hash = PasswordHash.createHash(rawPassword);
        YamlConfiguration passwords = getPasswords();
        passwords.set(playerName, hash);
        savePasswords(passwords);
    }

}
