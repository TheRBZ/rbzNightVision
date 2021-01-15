package me.therbz.rbznightvision;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class RbzNightVision extends JavaPlugin {
    private static HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>();

    @Override
    public void onEnable() {
        // Get configuration, and save the default if there is no file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

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

    public static boolean playerHasCooldown(UUID playerUUID) {
        return cooldowns.containsKey(playerUUID);
    }
    public static Long playerGetCooldown(UUID playerUUID) {
        return cooldowns.get(playerUUID);
    }
    public static void playerSetCooldown(UUID playerUUID, Long time) {
        cooldowns.put(playerUUID, time);
    }
}