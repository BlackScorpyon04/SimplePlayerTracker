package me.jordan.simpleplayertracker;

import me.jordan.simpleplayertracker.Listeners.InteractEvent;
import me.jordan.simpleplayertracker.Util.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrackingManager {

    private final Main plugin;
    private final boolean isFolia;
    // Store task references so we can cancel them
    private final Map<UUID, Object> activeTasks = new HashMap<>();

    public TrackingManager(Main plugin, boolean isFolia) {
        this.plugin = plugin;
        this.isFolia = isFolia;
    }

    public void startTracking(Player p) {
        // Cancel existing task if already tracking
        stopTracking(p);

        if (isFolia) {
            // Folia — schedule on player's own region thread
            var task = p.getScheduler().runAtFixedRate(plugin, scheduledTask -> {
                tickPlayer(p);
            }, () -> {
                // Player left — clean up
                activeTasks.remove(p.getUniqueId());
                InteractEvent.players.remove(p.getUniqueId());
            }, 1L, 20L);

            if (task != null) {
                activeTasks.put(p.getUniqueId(), task);
            }
        } else {
            // Paper — use global scheduler, store task ID
            var task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                tickPlayer(p);
            }, 1L, 20L);
            activeTasks.put(p.getUniqueId(), task);
        }
    }

    public void stopTracking(Player p) {
        Object task = activeTasks.remove(p.getUniqueId());
        if (task == null) return;

        if (isFolia) {
            ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) task).cancel();
        } else {
            ((org.bukkit.scheduler.BukkitTask) task).cancel();
        }
    }

    public void stopAll() {
        // Call on plugin disable
        activeTasks.forEach((uuid, task) -> {
            if (isFolia) {
                ((io.papermc.paper.threadedregions.scheduler.ScheduledTask) task).cancel();
            } else {
                ((org.bukkit.scheduler.BukkitTask) task).cancel();
            }
        });
        activeTasks.clear();
    }

    private void tickPlayer(Player p) {
        if (!p.isOnline()) {
            stopTracking(p);
            return;
        }

        Player toTrack = Bukkit.getPlayer(InteractEvent.players.get(p.getUniqueId()));

        if (toTrack == null) {
            toTrack = PlayerUtils.findNearest(p, plugin);
        }

        if (toTrack == null || !toTrack.isOnline()) return;

        String distance;
        int distancei = 0;

        if (p.getWorld().equals(toTrack.getWorld())) {
            distancei = (int) p.getLocation().distance(toTrack.getLocation());
            distance = distancei + "m away";
        } else {
            distance = "In Another World";
        }

        // Max distance check
        int maxDist = plugin.getConfig().getInt("maxdistance");
        if (maxDist > 0 && distancei > maxDist) {
            p.sendActionBar(Component.text("Player is too far away to track")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return;
        }

        // Min distance check
        int minDist = plugin.getConfig().getInt("mindistance");
        if (minDist > 0 && distancei < minDist) {
            p.sendActionBar(Component.text("Player is too close to track")
                    .color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return;
        }

        // Update compass
        if (toTrack.getWorld().equals(p.getWorld())) {
            updateCompass(p, toTrack);
        }

        // Action bar
        if (plugin.getConfig().getBoolean("showdistance")) {
            if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                final String finalDistance = distance;
                final Player finalToTrack = toTrack;
                p.sendActionBar(Component.text(finalToTrack.getName() + " is " + finalDistance));
            }
        }
    }

    private void updateCompass(Player p, Player toTrack) {
        for (ItemStack i : p.getInventory().getContents()) {
            if (i == null) continue;
            ItemMeta meta = i.getItemMeta();
            if (meta == null) continue;

            String displayName = PlainTextComponentSerializer.plainText()
                    .serialize(meta.displayName() != null ? meta.displayName() : Component.empty());

            if (displayName.contains("Tracking")) {
                CompassMeta cMeta = (CompassMeta) i.getItemMeta();
                cMeta.setLodestone(toTrack.getLocation());
                cMeta.setLodestoneTracked(false);
                i.setItemMeta(cMeta);
            }
        }
    }
}