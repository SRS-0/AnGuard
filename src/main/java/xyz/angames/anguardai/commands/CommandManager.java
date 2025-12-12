package xyz.angames.anguardai.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.angames.anguardai.Anguardai;
import xyz.angames.anguardai.data.PlayerProfile;
import xyz.angames.anguardai.managers.InfoManager;
import xyz.angames.anguardai.managers.PlayerProfileManager;

import java.util.List;

public class CommandManager implements CommandExecutor {

    private final Anguardai plugin;
    private final InfoManager infoManager;
    private final PlayerProfileManager profileManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public CommandManager(Anguardai plugin, InfoManager infoManager, PlayerProfileManager profileManager) {
        this.plugin = plugin;
        this.infoManager = infoManager;
        this.profileManager = profileManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("anguard.admin")) {
            sender.sendMessage(getMsg("messages.no_permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(getMsg("messages.invalid_usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            String raw = getRaw("messages.player_not_found").replace("{player}", args[1]);
            sender.sendMessage(format(raw));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "info":
                if (!(sender instanceof Player playerSender)) {
                    sender.sendMessage(getMsg("messages.player_only"));
                    return true;
                }
                this.infoManager.toggleInfoDisplay(playerSender, target);
                break;

            case "profile":
                displayProfile(sender, target);
                break;

            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void displayProfile(CommandSender sender, Player target) {
        PlayerProfile profile = this.profileManager.getProfile(target);
        if (profile != null) {
            String clientBrand = target.getClientBrandName();
            String clientDisplay = clientBrand != null ? clientBrand : "Unknown";

            sender.sendMessage(getMsg("messages.profile_header"));

            List<String> lines = plugin.getConfig().getStringList("messages.profile_lines");
            for (String line : lines) {
                String formatted = line
                        .replace("{player}", target.getName())
                        .replace("{ping}", String.valueOf(target.getPing()))
                        .replace("{client}", clientDisplay)
                        .replace("{version}", String.valueOf(target.getProtocolVersion()))
                        .replace("{suspicion}", String.format("%.2f", profile.suspicion))
                        .replace("{buffer}", String.format("%.2f", profile.buffer));

                sender.sendMessage(format(formatted));
            }

            sender.sendMessage(getMsg("messages.profile_footer"));
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(format("<dark_gray><st>       </st> <#8D98FC><bold>AnGuard Help</bold> <dark_gray><st>       </st>"));
        sender.sendMessage(format("<gray>/agac info <player> <dark_gray>- <white>Toggle tracker"));
        sender.sendMessage(format("<gray>/agac profile <player> <dark_gray>- <white>View profile"));
    }

    private String getRaw(String path) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String msg = plugin.getConfig().getString(path, "<red>Message missing: " + path);
        return prefix + msg;
    }

    private Component getMsg(String path) {
        return format(getRaw(path));
    }

    private Component format(String text) {
        return mm.deserialize(text);
    }
}