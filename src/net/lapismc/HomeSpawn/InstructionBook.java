package net.lapismc.HomeSpawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class InstructionBook {

    public static ItemStack getBook() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle(ChatColor.GOLD + "How To HomeSpawn");
        meta.setAuthor(ChatColor.AQUA + "Dart2112");
        if (!Bukkit.getServer().getOnlineMode()) {
            meta.addPage(ChatColor.GOLD
                    + "How To Use HomeSpawn! \n"
                    + ChatColor.RED
                    + "/home:"
                    + ChatColor.GOLD
                    + " Sends You To Your Home \n"
                    + // line 1
                    ChatColor.RED
                    + "/sethome:"
                    + ChatColor.GOLD
                    + " Sets Your Home At Your Current Location \n"
                    + // line 2
                    ChatColor.RED
                    + "/delhome:"
                    + ChatColor.GOLD
                    + " Removes Your Home \n"
                    + // line 3
                    ChatColor.RED
                    + "/spawn:"
                    + ChatColor.GOLD
                    + " Sends You To Spawn \n" // line 4
                    + ChatColor.RED + "/homepassword help:\n"
                    + ChatColor.GOLD
                    + "Displays The Home Transfer Commands \n" // line 5
                    + ChatColor.GREEN
                    + "For More Detailed Help Use /homespawn help");
        } else {
            meta.addPage(ChatColor.GOLD
                    + "How To Use HomeSpawn! \n"
                    + ChatColor.RED
                    + "/home:"
                    + ChatColor.GOLD
                    + " Sends You To Your Home \n"
                    + // line 1
                    ChatColor.RED
                    + "/sethome:"
                    + ChatColor.GOLD
                    + " Sets Your Home At Your Current Location \n"
                    + // line 2
                    ChatColor.RED + "/delhome:"
                    + ChatColor.GOLD
                    + " Removes Your Home \n"
                    + // line 3
                    ChatColor.RED + "/spawn:"
                    + ChatColor.GOLD
                    + " Sends You To Spawn \n" // line 4
                    + ChatColor.GREEN
                    + "For More Detailed Help Use /homespawn help");
        }
        book.setItemMeta(meta);
        return book;
    }

}
