package xyz.angames.anguardai.checks;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.angames.anguardai.Anguardai;
import xyz.angames.anguardai.data.PlayerProfile;

public abstract class Check {

    protected final Player player;
    protected final PlayerProfile profile;
    protected final Anguardai plugin;

    private int violationLevel = 0;

    public Check(Player player, PlayerProfile profile, Anguardai plugin) {
        this.player = player;
        this.profile = profile;
        this.plugin = plugin;
    }

    public void onMove(PlayerMoveEvent event) {
    }

    public void onDamage(EntityDamageByEntityEvent event) {
    }

    protected void flag(String debugInfo) {
        this.violationLevel++;
    }
}