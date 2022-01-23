package me.jordan.simpleplayertracker.Listeners;

import java.io.IOException;
import java.util.ArrayList;

import me.jordan.simpleplayertracker.Main;
import me.jordan.simpleplayertracker.Util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class InventoryClick implements Listener{

    private Main plugin;

    public InventoryClick(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) throws IOException {
        String title;
        if (e.getInventory().getType() != InventoryType.PLAYER) {
            title = e.getView().getTitle();
        }else {
            title = "";
        }
        if (e.getCurrentItem() != null) {
            Player p = (Player) e.getWhoClicked();
            if (title.equals("Players To Track")) {
                e.setCancelled(true);
                if (e.getCurrentItem().getType() == Material.PLAYER_HEAD || e.getCurrentItem().getType() == Material.ARROW) {
                    String playerName = null;
                    if (e.getCurrentItem().getItemMeta().hasLore()) {
                        playerName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0));
                    }

                    if (playerName == null){
                        playerName = Utils.color("&fNearest Player");
                        Player nearest = null;
                        for (Player plr : Bukkit.getOnlinePlayers()) {
                            if (plr.getUniqueId().equals(p.getUniqueId())) {
                                continue;
                            }
                            if (!plr.hasPermission("pt.bypass")) {
                                if (nearest == null) {
                                    nearest = plr;
                                }else {
                                    if (p.getWorld().equals(plr.getWorld())) {
                                        if (p.getLocation().distance(plr.getLocation()) < p.getLocation().distance(nearest.getLocation())) {
                                            nearest = plr;
                                        }
                                    }else {
                                        continue;
                                    }
                                }
                            }
                        }
                        if (nearest != null){
                            p.setCompassTarget(nearest.getLocation());
                            InteractEvent.players.put(p, null);
                        }
                    }else {
                        Player clickedPlayer = Bukkit.getPlayer(playerName);
                        p.setCompassTarget(clickedPlayer.getLocation());
                        InteractEvent.players.put(p, clickedPlayer);
                    }

                    setName(p.getItemInHand(), "Tracking");
                    setLore(p.getItemInHand(), playerName);
                    p.closeInventory();
                }
            }
        }
    }

    public ItemStack setName(ItemStack is, String name){
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(name);
        is.setItemMeta(m);
        return is;
    }

    public ItemStack setLore(ItemStack is, String one){
        ItemMeta meta = is.getItemMeta();
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(one);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return is;
    }
}
