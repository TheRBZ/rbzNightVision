package me.therbz.rbznightvision;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    private final JavaPlugin plugin = RbzNightVision.getPlugin(RbzNightVision.class);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if(p.hasPermission("rbznv.onjoin")) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, plugin.getConfig().getInt("nvticks"), 0));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.nvenable"))));
        }
    }
}
