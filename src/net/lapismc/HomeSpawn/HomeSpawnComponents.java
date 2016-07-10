package net.lapismc.HomeSpawn;


import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class HomeSpawnComponents {

    HomeSpawn plugin;
    File f;
    YamlConfiguration comp;

    public HomeSpawnComponents() {
    }

    public void init(HomeSpawn plugin) {
        this.plugin = plugin;
        f = new File(plugin.getDataFolder().getAbsolutePath()
                + File.separator + "Components.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {

            }
            comp = YamlConfiguration.loadConfiguration(f);
            comp.set("Homes", true);
            comp.set("Spawn", true);
            comp.set("HomeSpawnPassword", true);
            comp.set("API", true);
        }
        comp = YamlConfiguration.loadConfiguration(f);
        HomeSpawnCommand hsc = new HomeSpawnCommand(plugin);
        if (home()) {
            plugin.getCommand("home").setExecutor(hsc);
            plugin.getCommand("sethome").setExecutor(hsc);
            plugin.getCommand("delhome").setExecutor(hsc);
            plugin.getCommand("homeslist").setExecutor(hsc);
        }
        if (spawn()) {
            plugin.getCommand("spawn").setExecutor(hsc);
            plugin.getCommand("setspawn").setExecutor(hsc);
            plugin.getCommand("delspawn").setExecutor(hsc);
        }
        if (password()) {
            plugin.getCommand("homepassword").setExecutor(hsc);
        }
        plugin.getCommand("homespawn").setExecutor(hsc);

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

    public boolean api() {
        return comp.getBoolean("API");
    }
}
