package xyz.angames.anguardai;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.angames.anguardai.client.AIServer;
import xyz.angames.anguardai.commands.CommandManager;
import xyz.angames.anguardai.listeners.PlayerListener;
import xyz.angames.anguardai.managers.*;

public class Anguardai extends JavaPlugin {

    private static Anguardai instance;
    private static JavaPlugin spigotLoader;

    private InfoManager infoManager;
    private PlayerProfileManager playerProfileManager;
    private PacketManager packetManager;
    private AIServer aiServer;

    @Override
    public void onEnable() {
        instance = this;
        spigotLoader = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultConfig();
        getLogger().info("§b[AnGuard] §fCore module starting...");

        try {
            this.playerProfileManager = new PlayerProfileManager(this);
            this.infoManager = new InfoManager(this, this.playerProfileManager);
            this.aiServer = new AIServer(this);
            this.packetManager = new PacketManager(this, this.playerProfileManager);
            if (getCommand("anguard") != null) {
                getCommand("anguard").setExecutor(new CommandManager(this, this.infoManager, this.playerProfileManager));
            }
            getServer().getPluginManager().registerEvents(new PlayerListener(this.playerProfileManager), this);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (playerProfileManager != null) {
                        playerProfileManager.tickAllProfiles();
                    }
                }
            }.runTaskTimer(this, 0L, 1L);

            getLogger().info("§a[AnGuard] §fCore module has been enabled successfully!");

        } catch (Exception e) {
            getLogger().severe("§c[AnGuard] Critical Error! Is ProtocolLib installed? ProtocolLib 5.4.0 ");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (this.infoManager != null) this.infoManager.shutdown();
        getLogger().info("§c[AnGuard] §fCore module has been disabled.");
    }

    public static Anguardai getInstance() { return instance; }
    public static JavaPlugin getSpigotLoader() { return spigotLoader; }
    public PlayerProfileManager getPlayerProfileManager() { return this.playerProfileManager; }
    public InfoManager getInfoManager() { return this.infoManager; }
    public PacketManager getPacketManager() { return this.packetManager; }
    public AIServer getAiServer() { return this.aiServer; }
}