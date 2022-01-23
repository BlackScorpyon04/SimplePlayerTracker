package me.jordan.simpleplayertracker.UI;

import java.util.ArrayList;

import me.jordan.simpleplayertracker.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayersUI {
    public static Inventory inv;
    public static String name;
    public static int inv_rows = 54;
    public static void initialize() {
        name = Utils.color("Players To Track");
        inv = Bukkit.createInventory(null, inv_rows);
    }

    @SuppressWarnings("deprecation")
    public static Inventory GUI(Player p) {
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, name);

        int slot = 0;

        ItemStack arrow = new ItemStack(Material.ARROW, 1, (short) 3);
        ItemMeta im = arrow.getItemMeta();
        im.setDisplayName(Utils.color("&fNearest Player"));
        arrow.setItemMeta(im);
        inv.setItem(slot, arrow);
        slot++;

        for(Player pl : Bukkit.getOnlinePlayers()) {
            if (pl != p) {
                if (!pl.hasPermission("pt.bypass")) {
                    ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
                    SkullMeta sm = (SkullMeta) playerSkull.getItemMeta();
                    sm.setOwner(pl.getName());
                    sm.setDisplayName(Utils.color("&2" + pl.getDisplayName()));
                    ArrayList<String> lore = new ArrayList<String>();
                    lore.add(pl.getName());
                    sm.setLore(lore);
                    playerSkull.setItemMeta(sm);
                    inv.setItem(slot, playerSkull);
                    slot++;
                }
            }
        }




        toReturn.setContents(inv.getContents());
        return toReturn;
    }
}