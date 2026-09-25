package me.jordan.simpleplayertracker.UI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayersUI {

    public static String name;
    public static int inv_rows = 54;

    public static void initialize() {
        name = "Players To Track";
    }

    public static Inventory GUI(Player p) {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows,
                Component.text(name));

        int slot = 0;

        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta im = arrow.getItemMeta();
        if (im != null) {
            im.displayName(Component.text("Nearest Player", NamedTextColor.WHITE));
            arrow.setItemMeta(im);
        }
        toReturn.setItem(slot, arrow);
        slot++;

        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (pl.equals(p)) continue;
            if (pl.hasPermission("pt.bypass")) continue;

            ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta sm = (SkullMeta) playerSkull.getItemMeta();
            if (sm != null) {
                sm.setOwningPlayer(pl);
                sm.displayName(Component.text(pl.getName(), NamedTextColor.GREEN));
                sm.lore(List.of(Component.text(pl.getName())));
                playerSkull.setItemMeta(sm);
            }
            toReturn.setItem(slot, playerSkull);
            slot++;
        }

        return toReturn;
    }
}