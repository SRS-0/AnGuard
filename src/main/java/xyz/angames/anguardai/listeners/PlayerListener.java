package xyz.angames.anguardai.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.angames.anguardai.managers.PlayerProfileManager;

public class PlayerListener implements Listener {
    private final PlayerProfileManager profileManager;

    public PlayerListener(PlayerProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.profileManager.createProfile(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.profileManager.removeProfile(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        this.profileManager.getChecks(event.getPlayer()).forEach(check -> check.onMove(event));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player damager) {
            this.profileManager.getChecks(damager).forEach(check -> check.onDamage(event));
        }
        if (event.getEntity() instanceof Player victim) {
            this.profileManager.getChecks(victim).forEach(check -> check.onDamage(event));
        }
    }
}