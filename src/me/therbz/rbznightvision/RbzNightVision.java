package me.therbz.rbznightvision;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class RbzNightVision extends JavaPlugin {
    HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>();

    @Override
    public void onEnable() {
        // Get configuration, and save the default if there is no file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Set up bStats metrics
        final int BSTATS_PLUGIN_ID = 9704;
        MetricsLite metrics = new MetricsLite(this, BSTATS_PLUGIN_ID);

        // Check that the config is up-to-date
        final int CURRENT_CONFIG_VERSION = 3; // Update this as necessary
        int config_version = getConfig().getInt("config-version");

        if(config_version < CURRENT_CONFIG_VERSION) {
            getLogger().warning("Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
        }

        // If all is good so far, send an enabled message
        getLogger().info("Enabled rbzNightVision v" + getDescription().getVersion() + " by therbz");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if(cmd.getName().equalsIgnoreCase("rbznvreload")) {

            // Check that the sender has permission to reload
            if (!sender.hasPermission("rbznv.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.no-permission")));
                return true;
            }

            // Reload the config
            reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.reload")));
            getLogger().info("Reloaded rbzNightVision v" + getDescription().getVersion() + " by therbz");
            return true;
        }

        if(cmd.getName().equalsIgnoreCase("nightvision")) {
            if (args.length == 0) {

                // Check that the sender is a player
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.not-player")));
                    return true;
                }
                Player p = (Player) sender;

                // Check that the sender has permissions for self
                if (!sender.hasPermission("rbznv.use")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.no-permission")));
                    return true;
                }

                // Check for cooldown
                long cooldownTime = getConfig().getLong("cooldown");
                if (cooldownTime>0 && cooldowns.containsKey(p.getUniqueId()) && !sender.hasPermission("rbznv.cooldown.bypass")) {
                    long timeSinceCommandInMillis = System.currentTimeMillis() - cooldowns.get(p.getUniqueId());
                    if(timeSinceCommandInMillis < cooldownTime * 1000) {
                        String timeRemaning = String.valueOf(Math.round(cooldownTime-timeSinceCommandInMillis*0.001));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.cooldown").replace("%seconds%", timeRemaning)));
                        return true;
                    }
                }

                // If has NV, disable NV for player
                if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.nvdisable")));
                    return true;
                }

                // If does not have NV, enable NV for player
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, getConfig().getInt("nvticks"), 0));
                cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.nvenable")));
                return true;
            }

            if (args.length == 1) {

                // Check that the sender has permissions for others
                if (!sender.hasPermission("rbznv.use.others")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.other-player.no-permission")));
                    return true;
                }

                // Check that the target exists
                if (getPlayer(args[0]) == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.other-player.unknown-target")));
                    return true;
                }
                // If the target does exist, set it
                final Player target = getPlayer(args[0]);

                // If target has NV, disable NV for target
                if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    target.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.other-player.nvdisable-sender").replace("%target%", target.getName())));
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.nvdisable")));
                    return true;
                }

                // If target does not have NV, enable NV for target
                target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, getConfig().getInt("nvticks"), 0));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.other-player.nvenable-sender").replace("%target%", target.getName())));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.nvenable")));
                return true;
            }

            // User didn't give 0 or 1 arguments, so give them a incorrect usage message
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.incorrect-usage").replace("%usage%", cmd.getUsage())));
            return true;
        }

        return false;
    }
}