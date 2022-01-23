package me.jordan.simpleplayertracker;

import me.jordan.simpleplayertracker.Commands.Compass;
import me.jordan.simpleplayertracker.Listeners.InteractEvent;
import me.jordan.simpleplayertracker.Listeners.InventoryClick;
import me.jordan.simpleplayertracker.UI.PlayersUI;
import me.jordan.simpleplayertracker.Util.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{


    public static Main plugin;

    public void onEnable() {
        plugin = this;

        track();

        PlayersUI.initialize();

        new InteractEvent(this);
        new InventoryClick(this);
        new Compass(this);

        saveDefaultConfig();
    }

    public void track() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            for (Player p : InteractEvent.players.keySet()) {
                String distance;
                int distancei = 0;
                Player toTrack = InteractEvent.players.get(p);
                if (toTrack == null) {
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
                                    if (!nearest.getWorld().equals(p.getWorld())) {
                                        nearest = plr;
                                    }else if (p.getLocation().distance(plr.getLocation()) < p.getLocation().distance(nearest.getLocation())) {
                                        nearest = plr;
                                    }
                                }else {
                                    distance = "In Another World";
                                }
                            }
                        }
                    }
                    toTrack = nearest;
                }
                if (p.getWorld().equals(toTrack.getWorld())) {
                    distance = String.valueOf((int) p.getLocation().distance(toTrack.getLocation())) + "m away";
                    distancei = (int) p.getLocation().distance(toTrack.getLocation());
                }else {
                    distance = "In Another World";
                }
                if (plugin.getConfig().getInt("maxdistance") == 0) {

                }else if (distancei > plugin.getConfig().getInt("maxdistance")) {
                    distance =  Utils.color("&4Player is to far away to track");
                    return;
                }
                if (plugin.getConfig().getInt("mindistance") == 0) {

                }else if (distancei < plugin.getConfig().getInt("mindistance")) {
                    distance = Utils.color("&4Player is to close to track");
                    return;
                }

                if (toTrack.getLocation().getWorld() == p.getLocation().getWorld()){
                    for (ItemStack i : p.getInventory().getContents()) {
                        if (i != null && i.getItemMeta().getDisplayName().contains("Tracking")){
                            CompassMeta cMeta = (CompassMeta) i.getItemMeta();
                            cMeta.setLodestone(toTrack.getLocation());
                            cMeta.setLodestoneTracked(false);

                            i.setItemMeta(cMeta);
                        }
                    }
                }

//                if (toTrack.getLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
//                    if (p.getLocation().getWorld().getEnvironment() == World.Environment.NETHER){
//                        for (ItemStack i : p.getInventory().getContents()) {
//                            if (i != null) {
//                                if (i.getType() == Material.COMPASS) {
//                                    if (i.getItemMeta().getDisplayName().equalsIgnoreCase("Tracking")) {
//                                        CompassMeta cMeta = (CompassMeta) i.getItemMeta();
//                                        cMeta.setLodestone(toTrack.getLocation());
//                                        cMeta.setLodestoneTracked(false);
//                                        cMeta.setDisplayName("Tracking In Nether");
//
//                                        i.setItemMeta(cMeta);
//                                    } else if (i.getItemMeta().getDisplayName().equalsIgnoreCase("Tracking In Nether")) {
//                                        CompassMeta cMeta = (CompassMeta) i.getItemMeta();
//                                        cMeta.setLodestone(toTrack.getLocation());
//                                        i.setItemMeta(cMeta);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }else {
//                    if (p.getLocation().getWorld().equals(toTrack.getLocation().getWorld())) {
//                        p.setCompassTarget(toTrack.getLocation());
//                        for (ItemStack i : p.getInventory().getContents()) {
//                            if (i != null) {
//                                if (i.getType() == Material.COMPASS) {
//                                    if (i.getItemMeta().getDisplayName().equalsIgnoreCase("Tracking In Nether")) {
//                                        CompassMeta cMeta = (CompassMeta) i.getItemMeta();
//                                        ItemStack newCompass = new ItemStack(Material.COMPASS);
//                                        ItemMeta meta = newCompass.getItemMeta();
//                                        meta.setDisplayName("Tracking");
//                                        meta.setLore(cMeta.getLore());
//                                        i.setItemMeta(meta);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
                if (plugin.getConfig().getBoolean("showdistance")) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(toTrack.getDisplayName() + " is " + distance));
                    }
                }
            }
        }, 1L , 20);
    }
}

