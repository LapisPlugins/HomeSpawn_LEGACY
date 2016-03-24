package API;

import net.lapismc.HomeSpawn.HomeSpawn;
import net.lapismc.HomeSpawn.PasswordHash;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * API Class to get Config Files
 *
 * @author Dart2112
 */
class Configs {
    private final HomeSpawn plugin;

    public Configs(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads all configs from disk
     *
     * @throws IOException
     */
    public void reloadConfigs() throws IOException {
        this.plugin.reload("Silent");
    }

    /**
     * Returns the currently loaded Spawn.yml
     */
    public YamlConfiguration getSpawnConfig() {
        return this.plugin.spawn;
    }

    /**
     * Saves the given YamlConfiguration as the Spawn.yml file
     *
     * @throws IOException
     */
    public void saveSpawnConfig(YamlConfiguration SpawnConfig) throws IOException {
        this.plugin.spawn = SpawnConfig;
        this.plugin.spawn.save(this.plugin.spawnFile);
        this.plugin.reload("Silent");
    }

    /**
     * Returns the currently loaded Messages.yml
     */
    public YamlConfiguration getMessagesConfig() {
        return this.plugin.messages;
    }

    /**
     * Saves the given YamlConfiguration as the Messages.yml file
     *
     * @throws IOException
     */
    public void saveMessagesConfig(YamlConfiguration MessagesConfig) throws IOException {
        this.plugin.messages = MessagesConfig;
        this.plugin.messages.save(this.plugin.messagesFile);
        this.plugin.reload("Silent");
    }

    /**
     * Returns the currently loaded Passwords.yml
     */
    private YamlConfiguration getPasswords() {
        return this.plugin.passwords;
    }

    /**
     * Saves the given YamlConfiguration as the Passwords.yml file
     *
     * @throws IOException
     */
    private void savePasswords(YamlConfiguration Passwords) throws IOException {
        this.plugin.passwords = Passwords;
        this.plugin.passwords.save(this.plugin.passwordsFile);
        this.plugin.reload("Silent");
    }

    /**
     * Checks if the given string matches the password on file
     *
     * @throws NoSuchAlgorithmException and InvalidKeySpecException
     */
    public boolean checkPassword(String playerName, String Password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hash = this.getPasswords().getString(playerName);
        return PasswordHash.validatePassword(Password, hash);
    }

    /**
     * Hashes the password and saves it to as given username then saves the passwords config
     *
     * @throws IOException, NoSuchAlgorithmException and InvalidKeySpecException
     */
    public void setPassword(String playerName, String rawPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String hash = PasswordHash.createHash(rawPassword);
        YamlConfiguration passwords = this.getPasswords();
        passwords.set(playerName, hash);
        this.savePasswords(passwords);
    }

}