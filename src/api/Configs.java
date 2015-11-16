package API;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

/**
 * API Class to get Config Files
 *
 * @author Dart2112
 */
public class Configs {
    private HomeSpawn plugin;

    public Configs(HomeSpawn plugin) {
        this.plugin = plugin;
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

    //TODO check password

    //TODO set password

}