package me.jordan.simpleplayertracker.Commands;

import me.jordan.simpleplayertracker.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Compass implements CommandExecutor {

    public Compass(Main plugin) {
        plugin.getCommand("ptcompass").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        if (p.hasPermission("pt.compass")) {
            p.getInventory().addItem(new ItemStack(Material.COMPASS));
            p.sendMessage(Component.text("You received a tracking compass.", NamedTextColor.GREEN));
        } else {
            p.sendMessage(Component.text("You don't have permission to use this command.", NamedTextColor.RED));
        }
        return true;
    }
}