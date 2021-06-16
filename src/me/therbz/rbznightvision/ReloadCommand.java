package me.therbz.rbznightvision;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final NightVision plugin;

    public ReloadCommand(NightVision plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("rbznvreload")) {

            // Check that the sender has permission to reload
            if (!sender.hasPermission("rbznv.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission")));
                return true;
            }

            // Reload the config
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reload")));
            plugin.getLogger().info("Reloaded rbzNightVision v" + plugin.getDescription().getVersion() + " by therbz");
            return true;
        }
        return false;
    }
}
