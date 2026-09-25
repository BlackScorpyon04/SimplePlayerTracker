package me.jordan.simpleplayertracker;

import me.jordan.simpleplayertracker.Commands.Compass;
import me.jordan.simpleplayertracker.Listeners.InteractEvent;
import me.jordan.simpleplayertracker.Listeners.InventoryClick;
import me.jordan.simpleplayertracker.UI.PlayersUI;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{


    public static Main plugin;
    public static TrackingManager trackingManager;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        boolean foliaDetected = isFolia();
        trackingManager = new TrackingManager(this, foliaDetected);

        PlayersUI.initialize();

        new InteractEvent(this);
        new InventoryClick(this);
        new Compass(this);
    }

    @Override
    public void onDisable() {
        trackingManager.stopAll();
    }

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

