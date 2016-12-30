package net.lapismc.HomeSpawn;


import net.lapismc.HomeSpawn.api.HomeSpawnConfigs;
import net.lapismc.HomeSpawn.api.HomeSpawnPlayerData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class HomeSpawnComponents {

    HomeSpawn plugin;
    File f;
    YamlConfiguration comp;


    public void init(HomeSpawn plugin) {
        this.plugin = plugin;
        f = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "Components.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
                comp = YamlConfiguration.loadConfiguration(f);
                comp.set("Homes", true);
                comp.set("Spawn", true);
                comp.set("HomeSpawnPassword", false);
                comp.set("Logging", false);
                comp.set("API", false);
                comp.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        comp = YamlConfiguration.loadConfiguration(f);
        plugin.HSCommand = new HomeSpawnCommand(plugin);
        HomeSpawnConfigs c = new HomeSpawnConfigs(plugin);
        c.init(plugin);
        HomeSpawnPlayerData pd = new HomeSpawnPlayerData(plugin);
        pd.init(plugin);
        if (home()) {
            plugin.getCommand("home").setExecutor(plugin.HSCommand);
            plugin.getCommand("sethome").setExecutor(plugin.HSCommand);
            plugin.getCommand("delhome").setExecutor(plugin.HSCommand);
            plugin.getCommand("homeslist").setExecutor(plugin.HSCommand);
        }
        if (spawn()) {
            plugin.getCommand("spawn").setExecutor(plugin.HSCommand);
            plugin.getCommand("setspawn").setExecutor(plugin.HSCommand);
            plugin.getCommand("delspawn").setExecutor(plugin.HSCommand);
        }
        if (password()) {
            plugin.getCommand("homepassword").setExecutor(plugin.HSCommand);
        }
        plugin.getCommand("homespawn").setExecutor(plugin.HSCommand);
        plugin.HSCommand.registerCommands();
        plugin.logger.info("Commands Registered!");
    }

    public boolean home() {
        return comp.getBoolean("Homes");
    }

    public boolean spawn() {
        return comp.getBoolean("Spawn");
    }

    public boolean password() {
        return comp.getBoolean("HomeSpawnPassword");
    }

    public boolean logging() {
        return comp.getBoolean("Logging");
    }

    public boolean api() {
        return comp.getBoolean("API");
    }
}
