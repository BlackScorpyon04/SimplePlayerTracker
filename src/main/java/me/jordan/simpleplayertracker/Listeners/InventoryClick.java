package me.jordan.simpleplayertracker.Listeners;

import me.jordan.simpleplayertracker.Main;
import me.jordan.simpleplayertracker.Util.PlayerUtils;
import me.jordan.simpleplayertracker.Util.Utils;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClick implements Listener {

    private final Main plugin;

    public InventoryClick(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.PLAYER) return;
        if (e.getCurrentItem() == null) return;

        String title = PlainTextComponentSerializer.plainText()
                .serialize(e.getView().title());

        if (!title.equals("Players To Track")) return;

        e.setCancelled(true);

        Material type = e.getCurrentItem().getType();
        if (type != Material.PLAYER_HEAD && type != Material.ARROW) return;

        Player p = (Player) e.getWhoClicked();
        ItemMeta clickedMeta = e.getCurrentItem().getItemMeta();
        if (clickedMeta == null) return;

        String playerName = null;

        if (clickedMeta.lore() != null && !clickedMeta.lore().isEmpty()) {
            playerName = PlainTextComponentSerializer.plainText()
                    .serialize(clickedMeta.lore().getFirst());
        }

        // Get compass BEFORE closing inventory
        ItemStack compass = p.getInventory().getItemInMainHand();

        if (playerName == null) {
            Player nearest = PlayerUtils.findNearest(p, plugin);
            if (nearest != null) {
                p.setCompassTarget(nearest.getLocation());
                InteractEvent.players.put(p.getUniqueId(), null);
                Utils.setName(compass, "Tracking");
                Utils.setLore(compass, "Nearest Player");
                Main.trackingManager.startTracking(p); // START TASK
            }
        } else {
            Player clickedPlayer = Bukkit.getPlayer(playerName);
            if (clickedPlayer != null) {
                p.setCompassTarget(clickedPlayer.getLocation());
                InteractEvent.players.put(p.getUniqueId(), clickedPlayer.getUniqueId());
                Utils.setName(compass, "Tracking");
                Utils.setLore(compass, playerName);
                Main.trackingManager.startTracking(p); // START TASK
            }
        }

        p.closeInventory();
    }
}