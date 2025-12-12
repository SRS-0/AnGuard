package xyz.angames.anguardai.checks.impl;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.angames.anguardai.Anguardai;
import xyz.angames.anguardai.checks.Check;
import xyz.angames.anguardai.data.PlayerProfile;
import xyz.angames.anguardai.data.TickDataPOJO;
import xyz.angames.anguardai.flatbuffers.TickData;
import xyz.angames.anguardai.flatbuffers.TickDataSequence;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AimCheck extends Check {

    private final int sequence;
    private final int step;
    private static final ThreadLocal<FlatBufferBuilder> BUILDER = ThreadLocal.withInitial(() -> new FlatBufferBuilder(4096));
    private static final Gson GSON = new Gson();

    public AimCheck(Player player, PlayerProfile profile, Anguardai anguardai) {
        super(player, profile, anguardai);
        this.sequence = plugin.getConfig().getInt("ai.sequence", 40);
        this.step = plugin.getConfig().getInt("ai.step", 10);
    }

    @Override
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().equals(this.player)) {
            if (this.profile.damageMultiplier < 1.0) {
                event.setDamage(event.getDamage() * this.profile.damageMultiplier);
            }
            this.profile.isAttacking = true;
            this.profile.ticksSinceAttack = 0;
            this.profile.ticksSinceLastCombatAction = 0;
        }
        if (event.getEntity().equals(this.player)) {
            this.profile.ticksSinceLastCombatAction = 0;
        }
    }

    @Override
    public void onMove(PlayerMoveEvent event) {}

    public void handlePacketMovement(float toYaw, float toPitch, boolean onGround) {
        if (this.profile.lastYaw == toYaw && this.profile.lastPitch == toPitch) return;


        synchronized (this.profile) {
            float deltaYaw = (toYaw - this.profile.lastYaw + 540.0F) % 360.0F - 180.0F;
            float deltaPitch = toPitch - this.profile.lastPitch;
            this.profile.aimProcessor.process(deltaYaw, deltaPitch);
            TickDataPOJO tick = new TickDataPOJO(
                    deltaYaw, deltaPitch,
                    this.profile.aimProcessor.accelYaw,
                    this.profile.aimProcessor.accelPitch,
                    this.profile.aimProcessor.jerkYaw,
                    this.profile.aimProcessor.jerkPitch,
                    this.profile.aimProcessor.getGcdErrorYaw(deltaYaw),
                    this.profile.aimProcessor.getGcdErrorPitch(deltaPitch)
            );


            this.profile.addMovementFrame(tick);
            this.profile.ticksStep++;
            while (this.profile.movementHistory.size() > sequence) {
                this.profile.movementHistory.removeFirst();
            }

            this.profile.lastYaw = toYaw;
            this.profile.lastPitch = toPitch;
            this.updateProfile(toYaw, toPitch, deltaYaw, deltaPitch);
            if (this.profile.movementHistory.size() == sequence && this.profile.ticksStep >= step) {
                sendData();
                this.profile.ticksStep = 0;
            }
        }
    }

    private void sendData() {
        if (this.player.hasPermission("anguard.bypass")) return;

        List<TickDataPOJO> history = new ArrayList<>(this.profile.movementHistory);


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                byte[] binaryData = serialize(history);
                plugin.getAiServer().sendRequest(binaryData).thenAccept(this::onResponse);
            } catch (Exception e) {
            }
        });
    }

    private void onResponse(String response) {
        if (response == null) return;
        try {
            JsonObject json = GSON.fromJson(response, JsonObject.class);
            if (!json.has("confidence")) return;

            double probability = json.get("confidence").getAsDouble() / 100.0;

            Bukkit.getScheduler().runTask(plugin, () -> {
                this.profile.suspicion = probability * 100.0;

                double cheatProb = 0.90;
                double legitProb = 0.10;

                if (probability > cheatProb) {
                    this.profile.buffer += (probability - cheatProb) * plugin.getConfig().getDouble("ai.buffer.multiplier", 100.0);
                } else if (probability < legitProb) {
                    this.profile.buffer = Math.max(0, this.profile.buffer - plugin.getConfig().getDouble("ai.buffer.decrease", 0.25));
                }

                double drProb = plugin.getConfig().getDouble("ai.damage-reduction.suspicion", 0.9);
                if (probability >= drProb) {
                    double ratio = (probability - drProb) / (1.0 - drProb);
                    this.profile.damageMultiplier = 1.0 - Math.min(1.0, ratio);
                } else {
                    this.profile.damageMultiplier = 1.0;
                }

                if (this.profile.buffer > plugin.getConfig().getDouble("ai.buffer.flag", 50.0)) {
                    this.profile.violationLevel++;
                    this.profile.buffer = plugin.getConfig().getDouble("ai.buffer.reset-on-flag", 25.0);
                    if (plugin.getConfig().getBoolean("autoban.enable")) {
                        String cmd = plugin.getConfig().getString("autoban.command").replace("{player}", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    }
                }
            });
        } catch (Exception ignored) {}
    }


    private void resetProfile(float yaw, float pitch) {
        this.profile.lastYaw = yaw;
        this.profile.lastPitch = pitch;
        this.profile.lastDeltaYaw = 0;
        this.profile.lastDeltaPitch = 0;
        this.profile.lastAccelYaw = 0;
        this.profile.lastAccelPitch = 0;
        this.profile.aimProcessor.currentYawAccel = 0;
        this.profile.aimProcessor.currentPitchAccel = 0;
        this.profile.aimProcessor.jerkYaw = 0;
        this.profile.aimProcessor.jerkPitch = 0;
    }

    private void updateProfile(float currentYaw, float currentPitch, float deltaYaw, float deltaPitch) {
        float accelYaw = deltaYaw - this.profile.lastDeltaYaw;
        float accelPitch = deltaPitch - this.profile.lastDeltaPitch;

        this.profile.lastYaw = currentYaw;
        this.profile.lastPitch = currentPitch;
        this.profile.lastDeltaYaw = deltaYaw;
        this.profile.lastDeltaPitch = deltaPitch;
        this.profile.lastAccelYaw = accelYaw;
        this.profile.lastAccelPitch = accelPitch;
    }

    private byte[] serialize(List<TickDataPOJO> ticks) {
        FlatBufferBuilder builder = BUILDER.get();
        builder.clear();
        int[] tickOffsets = new int[ticks.size()];

        for (int i = 0; i < ticks.size(); i++) {
            TickDataPOJO t = ticks.get(i);

            TickData.startTickData(builder);
            TickData.addDeltaYaw(builder, t.deltaYaw);
            TickData.addDeltaPitch(builder, t.deltaPitch);
            TickData.addAccelYaw(builder, t.accelYaw);
            TickData.addAccelPitch(builder, t.accelPitch);
            TickData.addJerkYaw(builder, t.jerkYaw);
            TickData.addJerkPitch(builder, t.jerkPitch);
            TickData.addGcdErrorYaw(builder, t.gcdErrorYaw);
            TickData.addGcdErrorPitch(builder, t.gcdErrorPitch);
            tickOffsets[i] = TickData.endTickData(builder);
        }

        int ticksVector = TickDataSequence.createTicksVector(builder, tickOffsets);
        TickDataSequence.startTickDataSequence(builder);
        TickDataSequence.addTicks(builder, ticksVector);
        int endOffset = TickDataSequence.endTickDataSequence(builder);
        builder.finish(endOffset);
        ByteBuffer buf = builder.dataBuffer();
        byte[] arr = new byte[buf.remaining()];
        buf.get(arr);
        return arr;
    }
}