package xyz.angames.anguardai.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.angames.anguardai.Anguardai;
import xyz.angames.anguardai.checks.Check;
import xyz.angames.anguardai.checks.impl.AimCheck;
import xyz.angames.anguardai.data.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfileManager {

    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    private final Map<UUID, List<Check>> playerChecks = new ConcurrentHashMap<>();

    private final Anguardai anguardai;
    private final JavaPlugin loader;

    public PlayerProfileManager(Anguardai anguardai) {
        this.anguardai = anguardai;
        this.loader = Anguardai.getSpigotLoader();
    }

    public void createProfile(Player player) {
        PlayerProfile profile = new PlayerProfile(player.getLocation().getYaw(), player.getLocation().getPitch());
        this.profiles.put(player.getUniqueId(), profile);

        List<Check> checks = new ArrayList<>();

        if (this.loader.getConfig().getBoolean("checks.aim.enabled", true)) {
            checks.add(new AimCheck(player, profile, this.anguardai));
        }

        this.playerChecks.put(player.getUniqueId(), checks);
    }

    public void removeProfile(Player player) {
        this.profiles.remove(player.getUniqueId());
        this.playerChecks.remove(player.getUniqueId());
    }

    public PlayerProfile getProfile(Player player) {
        return this.profiles.get(player.getUniqueId());
    }

    public List<Check> getChecks(Player player) {
        return this.playerChecks.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }


    public void tickAllProfiles() {
        int decayTicks = this.loader.getConfig().getInt("violation_level.decay_seconds", 450) * 20;

        for (PlayerProfile profile : this.profiles.values()) {
            profile.ticksSinceAttack++;
            profile.ticksSinceLastCombatAction++;
            profile.ticksSinceLastViolation++;
            if (profile.isAttacking) {
                profile.isAttacking = false;
            }
            if (profile.violationLevel > 0 && decayTicks > 0 && profile.ticksSinceLastViolation >= decayTicks) {
                profile.violationLevel = Math.max(0, profile.violationLevel - 1);
                profile.ticksSinceLastViolation = 0;
            }
        }
    }
}