package me.jordan.simpleplayertracker.Commands;

import me.jordan.simpleplayertracker.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Compass implements CommandExecutor{

    private Main plugin;

    public Compass(Main plugin) {
        this.plugin = plugin;

        plugin.getCommand("ptcompass").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("YOUR NOT A PLAYER CANT EXECUTE.");
            return true;
        }

        Player p = (Player) sender;

        if (p.hasPermission("pt.compass")) {
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
            return true;
        }else {
            return false;
        }

    }

}
