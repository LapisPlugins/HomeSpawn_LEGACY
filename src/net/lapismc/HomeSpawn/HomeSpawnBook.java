/*
 * Copyright 2017 Benjamin Martin
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

package net.lapismc.HomeSpawn;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;

class HomeSpawnBook {

    private YamlConfiguration yaml;

    HomeSpawnBook(HomeSpawn plugin) {
        File f = new File(plugin.getDataFolder() + File.separator + "HomeSpawnBook.yml");
        yaml = YamlConfiguration.loadConfiguration(f);
    }

    ItemStack getBook() {
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
