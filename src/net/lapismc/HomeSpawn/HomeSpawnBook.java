package net.lapismc.HomeSpawn;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;

public class HomeSpawnBook {

    private HomeSpawn plugin;
    private File f;
    private YamlConfiguration yaml;

    public HomeSpawnBook(HomeSpawn plugin) {
        this.plugin = plugin;
        f = new File(plugin.getDataFolder() + File.separator + "HomeSpawnBook.yml");
        yaml = YamlConfiguration.loadConfiguration(f);
    }

    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(ChatColor.translateAlternateColorCodes
                ('&', yaml.getString("Title")));
        meta.setAuthor(ChatColor.AQUA + "Dart2112");
        int zero = 0;
        int pages = yaml.getInt("Book.NumbOfPages");
        while (pages > zero) {
            zero++;
            meta.addPage(ChatColor.translateAlternateColorCodes
                    ('&', yaml.getString("Book." + zero)));
        }
        book.setItemMeta(meta);
        return book;
    }

}
