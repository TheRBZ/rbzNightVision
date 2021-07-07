package me.therbz.rbznightvision;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class NightVision extends JavaPlugin {
    private HashMap<UUID, Long> cooldowns = new HashMap<>();
    public final int CURRENT_CONFIG_VERSION = 3;

    @Override
    public void onEnable() {
        // Get configuration, and save the default if there is no file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Register event listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register commands
        Objects.requireNonNull(getCommand("nightvision")).setExecutor(new NightVisionCommand(this));

        // Set up bStats metrics
        final int BSTATS_PLUGIN_ID = 9704;
        MetricsLite metrics = new MetricsLite(this, BSTATS_PLUGIN_ID);

        // Check that the config is up-to-date
        int config_version = getConfig().getInt("config-version");
        if(config_version < this.CURRENT_CONFIG_VERSION) {
            getLogger().warning("Your config.yml is outdated! Delete it (or rename it) and restart your server to update it!");
        }
    }

    public boolean playerHasCooldown(UUID playerUUID) {
        return cooldowns.containsKey(playerUUID);
    }
    public Long playerGetCooldown(UUID playerUUID) {
        return cooldowns.get(playerUUID);
    }
    public void playerSetCooldown(UUID playerUUID, Long time) {
        cooldowns.put(playerUUID, time);
    }
}