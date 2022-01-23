package me.jordan.simpleplayertracker.Listeners;

import java.util.HashMap;

import me.jordan.simpleplayertracker.Main;
import me.jordan.simpleplayertracker.UI.PlayersUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

public class InteractEvent implements Listener{

    private Main plugin;
    public static HashMap<Player,Player> players = new HashMap<Player,Player>();

    public InteractEvent(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("pt.track")) {
            if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS && (e.getAction() == Action.RIGHT_CLICK_BLOCK ||  e.getAction() == Action.RIGHT_CLICK_AIR)) {
                if (p.isSneaking()) {
                    p.openInventory(PlayersUI.GUI(p));
                }else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Tracking")) {
                    String playerName = (String) p.getInventory().getItemInMainHand().getItemMeta().getLore().toArray()[0];
                    Player toTrack = Bukkit.getPlayerExact(playerName);
                    if (toTrack != null) {
                        if (!toTrack.hasPermission("pt.bypass")) {
                            ItemStack i = p.getInventory().getItemInMainHand();
                            CompassMeta cMeta = (CompassMeta) i.getItemMeta();
                            cMeta.setLodestone(toTrack.getLocation());
                            cMeta.setLodestoneTracked(false);

                            i.setItemMeta(cMeta);

                            players.put(p, toTrack);
                        }
                    }
                }else {
                    p.openInventory(PlayersUI.GUI(p));
                }

            }
        }
    }

}
