package me.jordan.simpleplayertracker.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Utils {
    public static String color (String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static ItemStack createItem(Inventory inv, String materialID, int amount, int invSlot, String displayname, String...loreString) {
        ItemStack item;
        List<String> lore = new ArrayList();

        item = new ItemStack(Material.getMaterial(materialID), amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.color(displayname));
        for (String s : loreString) {
            lore.add(Utils.color(s));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        inv.setItem(invSlot - 1, item);
        return item;
    }
}
