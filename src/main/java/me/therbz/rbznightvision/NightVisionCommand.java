package me.therbz.rbznightvision;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.Bukkit.getPlayer;

public class NightVisionCommand implements CommandExecutor {
    private final NightVision plugin;

    public NightVisionCommand(NightVision plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if(cmd.getName().equalsIgnoreCase("nightvision")) {
            if (args.length == 0) {

                // Check that the sender is a player
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.not-player")));
                    return true;
                }
                Player p = (Player) sender;

                // Check that the sender has permissions for self
                if (!sender.hasPermission("rbznv.use")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission")));
                    return true;
                }

                // Check for cooldown
                long cooldownTime = plugin.getConfig().getLong("cooldown");

                if (cooldownTime>0 && plugin.playerHasCooldown(p.getUniqueId()) && !sender.hasPermission("rbznv.cooldown.bypass")) {
                    long timeSinceCommandInMillis = System.currentTimeMillis() - plugin.playerGetCooldown(p.getUniqueId());

                    if(timeSinceCommandInMillis < cooldownTime * 1000) {
                        String timeRemaning = String.valueOf(Math.round(cooldownTime-timeSinceCommandInMillis*0.001));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.cooldown").replace("%seconds%", timeRemaning)));
                        return true;
                    }
                }

                // If has NV, disable NV for player
                if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    plugin.playerSetCooldown(p.getUniqueId(), System.currentTimeMillis());
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nvdisable")));
                    return true;
                }

                // If does not have NV, enable NV for player
                p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, plugin.getConfig().getInt("nvticks"), 0));
                plugin.playerSetCooldown(p.getUniqueId(), System.currentTimeMillis());
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nvenable")));
                return true;
            }

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("reload")) {
                    // Check that the sender has permission to reload
                    if (!sender.hasPermission("rbznv.reload")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission")));
                        return true;
                    }

                    // Reload the config
                    plugin.reloadConfig();

                    // Check that the config is up-to-date
                    int config_version = plugin.getConfig().getInt("config-version");
                    if(config_version < plugin.CURRENT_CONFIG_VERSION) {
                        sender.sendMessage(ChatColor.RED + "Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
                    }

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.reload")));
                    plugin.getLogger().info("Reloaded rbzNightVision.");

                    return true;
                }

                // Check that the sender has permissions for nv on other players
                if (!sender.hasPermission("rbznv.use.others")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.other-player.no-permission")));
                    return true;
                }

                final Player target = getPlayer(args[0]);

                // Check that the target exists
                if (target == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.other-player.unknown-target")));
                    return true;
                }

                // If target has NV, disable NV for target
                if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    target.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.other-player.nvdisable-sender").replace("%target%", target.getName())));
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nvdisable")));
                    return true;
                }

                // Target does not have NV, enable NV for target
                target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, plugin.getConfig().getInt("nvticks"), 0));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.other-player.nvenable-sender").replace("%target%", target.getName())));
                target.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.nvenable")));
                return true;
            }

            // User didn't give 0 or 1 arguments, so give them a incorrect usage message
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.incorrect-usage").replace("%usage%", cmd.getUsage())));
            return true;
        }

        return false;
    }
}
