package xyz.angames.anguardai.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.angames.anguardai.Anguardai;
import xyz.angames.anguardai.data.PlayerProfile;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InfoManager {
    private final Anguardai anguardai;
    private final JavaPlugin loader;
    private final PlayerProfileManager profileManager;
    private final Map<UUID, BukkitTask> activeInfoTasks = new ConcurrentHashMap<>();

    private final MiniMessage mm = MiniMessage.miniMessage();

    public InfoManager(Anguardai anguardai, PlayerProfileManager profileManager) {
        this.anguardai = anguardai;
        this.profileManager = profileManager;
        this.loader = Anguardai.getSpigotLoader();
    }

    public void toggleInfoDisplay(Player sender, Player target) {
        UUID senderId = sender.getUniqueId();
        String prefix = anguardai.getConfig().getString("messages.prefix", "");

        if (this.activeInfoTasks.containsKey(senderId)) {
            BukkitTask task = this.activeInfoTasks.remove(senderId);
            if (task != null) task.cancel();

            sender.sendActionBar(Component.empty());
            String msg = anguardai.getConfig().getString("messages.tracker_disabled", "Stopped.")
                    .replace("{player}", target.getName());
            sender.sendMessage(format(prefix + msg));

        } else {
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (sender.isOnline() && target.isOnline()) {
                        PlayerProfile profile = profileManager.getProfile(target);
                        if (profile != null) {

                            String bufColor = "<green>";
                            if (profile.buffer > 5.0) bufColor = "<yellow>";
                            if (profile.buffer > 10.0) bufColor = "<red>";
                            String bufClose = bufColor.replace("<", "</");
                            String dmgColor = profile.damageMultiplier < 1.0 ? "<red>" : "<white>";
                            String dmgClose = dmgColor.replace("<", "</");
                            double sens = profile.aimProcessor.sensitivityX * 100;
                            String sensString;
                            if (sens <= 0) {
                                sensString = "<gray>Calc...</gray>";
                            } else {
                                sensString = String.format("<white>%.0f%%</white>", sens);
                            }
                            String bar = String.format(
                                    "<aqua>%s</aqua> <dark_gray>|</dark_gray> " +
                                            "<gray>Ping: <white>%d</white> <dark_gray>|</dark_gray> " +
                                            "<gray>VL: <red>%d</red> <dark_gray>|</dark_gray> " +
                                            "<gray>Buf: %s%.1f%s <dark_gray>|</dark_gray> " +
                                            "<gray>Sus: <#8D98FC>%.1f%%</#8D98FC> <dark_gray>|</dark_gray> " +
                                            "<gray>Sens: %s <dark_gray>|</dark_gray> " +
                                            "<gray>Dmg: %s%.2fx%s",

                                    target.getName(),
                                    target.getPing(),
                                    profile.violationLevel,
                                    bufColor, profile.buffer, bufClose,
                                    profile.suspicion,
                                    sensString,
                                    dmgColor, profile.damageMultiplier, dmgClose
                            );

                            sender.sendActionBar(format(bar));
                        }
                    } else {
                        this.cancel();
                        activeInfoTasks.remove(senderId);
                    }
                }
            }.runTaskTimer(this.loader, 0L, 1L);

            this.activeInfoTasks.put(senderId, task);

            String msg = anguardai.getConfig().getString("messages.tracker_enabled", "Tracking.")
                    .replace("{player}", target.getName());
            sender.sendMessage(format(prefix + msg));
        }
    }

    private Component format(String text) {
        return mm.deserialize(text);
    }

    public void shutdown() {
        this.activeInfoTasks.values().forEach(BukkitTask::cancel);
        this.activeInfoTasks.clear();
    }
}