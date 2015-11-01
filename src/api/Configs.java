package api;

import net.lapismc.HomeSpawn.HomeSpawn;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

public class Configs {
    private HomeSpawn plugin;

    public Configs(HomeSpawn plugin) {
        this.plugin = plugin;
    }

    public YamlConfiguration getSpawnConfig() {
        return plugin.spawn;
    }

    public void saveSpawnConfig(YamlConfiguration SpawnConfig) throws IOException {
        plugin.spawn = SpawnConfig;
        plugin.spawn.save(plugin.spawnFile);
        plugin.reload("Silent");
    }

    public YamlConfiguration getMessagesConfig() {
        return plugin.messages;
    }

    public void saveMessagesConfig(YamlConfiguration MessagesConfig) throws IOException {
        plugin.messages = MessagesConfig;
        plugin.messages.save(plugin.messagesFile);
        plugin.reload("Silent");
    }

    public YamlConfiguration getPasswords() {
        return plugin.passwords;
    }

    public void savePasswords(YamlConfiguration Passwords) throws IOException {
        plugin.passwords = Passwords;
        plugin.passwords.save(plugin.passwordsFile);
        plugin.reload("Silent");
    }

}