package me.jordan.simpleplayertracker.Util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerUtils {

    public static Player findNearest(Player p, Plugin plugin) {
        Player nearest = null;
        for (Player plr : plugin.getServer().getOnlinePlayers()) {
            if (plr.getUniqueId().equals(p.getUniqueId())) continue;
            if (plr.hasPermission("pt.bypass")) continue;

            if (nearest == null) {
                nearest = plr;
            } else if (p.getWorld().equals(plr.getWorld())) {
                if (!nearest.getWorld().equals(p.getWorld())) {
                    nearest = plr;
                } else if (p.getLocation().distance(plr.getLocation()) < p.getLocation().distance(nearest.getLocation())) {
                    nearest = plr;
                }
            }
        }
        return nearest;
    }
}
