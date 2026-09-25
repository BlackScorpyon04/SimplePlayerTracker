package me.jordan.simpleplayertracker.Util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String color(String s) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(
                LegacyComponentSerializer.legacyAmpersand().deserialize(s)
        );
    }

    public static Component colorComponent(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static ItemStack createItem(Inventory inv, String materialID, int amount, int invSlot, String displayname, String... loreString) {
        List<Component> lore = new ArrayList<>();

        ItemStack item = new ItemStack(Material.getMaterial(materialID), amount);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(colorComponent(displayname));
            for (String s : loreString) {
                lore.add(colorComponent(s));
            }
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        inv.setItem(invSlot - 1, item);
        return item;
    }

    public static void setName(ItemStack is, String name) {
        if (is == null) return;
        ItemMeta m = is.getItemMeta();
        if (m == null) return;
        m.displayName(Component.text(name));
        is.setItemMeta(m);
    }

    public static void setLore(ItemStack is, String... lines) {
        if (is == null) return;
        ItemMeta meta = is.getItemMeta();
        if (meta == null) return;
        List<Component> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(Component.text(line));
        }
        meta.lore(lore);
        is.setItemMeta(meta);
    }
}