package me.jordan.simpleplayertracker.Listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.jordan.simpleplayertracker.Main;
import me.jordan.simpleplayertracker.UI.PlayersUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class InteractEvent implements Listener {


    public static HashMap<UUID, UUID> players = new HashMap<>();

    public InteractEvent(Main plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("pt.track")) return;

        ItemStack held = p.getInventory().getItemInMainHand();
        if (held.getType() != Material.COMPASS) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemMeta meta = held.getItemMeta();
        if (meta == null) return;

        String displayName = PlainTextComponentSerializer.plainText().serialize(
                meta.displayName() != null ? meta.displayName() : Component.empty()
        );

        if (p.isSneaking()) {
            p.openInventory(PlayersUI.GUI(p));
        } else if (displayName.equals("Tracking")) {
            if (meta.lore() == null || meta.lore().isEmpty()) return;

            String playerName = PlainTextComponentSerializer.plainText()
                    .serialize(meta.lore().get(0));

            Player toTrack = Bukkit.getPlayerExact(playerName);
            if (toTrack != null && !toTrack.hasPermission("pt.bypass")) {
                CompassMeta cMeta = (CompassMeta) held.getItemMeta();
                cMeta.setLodestone(toTrack.getLocation());
                cMeta.setLodestoneTracked(false);
                held.setItemMeta(cMeta);
                players.put(p.getUniqueId(), toTrack.getUniqueId());
                Main.trackingManager.startTracking(p);
            }
        } else {
            p.openInventory(PlayersUI.GUI(p));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        InteractEvent.players.remove(p.getUniqueId());
        Main.trackingManager.stopTracking(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player joined = e.getPlayer();

        for (Map.Entry<UUID, UUID> entry : players.entrySet()) {
            UUID trackedUUID = entry.getValue();
            if (trackedUUID == null) continue;

            if (trackedUUID.equals(joined.getUniqueId())) {
                Player tracker = Bukkit.getPlayer(entry.getKey());
                if (tracker != null) {
                    Main.trackingManager.startTracking(tracker);
                }
            }
        }
    }
}