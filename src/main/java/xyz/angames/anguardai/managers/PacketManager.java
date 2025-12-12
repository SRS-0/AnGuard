package xyz.angames.anguardai.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import xyz.angames.anguardai.Anguardai;
import xyz.angames.anguardai.checks.Check;
import xyz.angames.anguardai.checks.impl.AimCheck;
import xyz.angames.anguardai.data.PlayerProfile;

import java.util.List;

public class PacketManager {

    public PacketManager(Anguardai plugin, PlayerProfileManager profileManager) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;
                PlayerProfile profile = profileManager.getProfile(player);
                if (profile == null) return;

                PacketContainer packet = event.getPacket();
                float yaw = packet.getFloat().read(0);
                float pitch = packet.getFloat().read(1);
                boolean onGround = packet.getBooleans().read(0);

                List<Check> checks = profileManager.getChecks(player);
                for (Check check : checks) {
                    if (check instanceof AimCheck aimCheck) {
                        aimCheck.handlePacketMovement(yaw, pitch, onGround);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                plugin, ListenerPriority.MONITOR, PacketType.Play.Server.POSITION
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;

                PlayerProfile profile = profileManager.getProfile(player);
                if (profile != null) {
                    profile.lastTeleportTime = System.currentTimeMillis();
                }
            }
        });
    }
}