package xyz.angames.anguardai.data;

import xyz.angames.anguardai.checks.math.AimProcessor;
import java.util.LinkedList;

public class PlayerProfile {

    public final AimProcessor aimProcessor;
    public float lastYaw, lastPitch;
    public float lastDeltaYaw, lastDeltaPitch;
    public float lastAccelYaw, lastAccelPitch;
    public int ticksSinceAttack = 1000;
    public boolean isAttacking = false;
    public int ticksSinceLastCombatAction = 1000;
    public long lastTeleportTime = 0;
    public int ticksSinceLastCheck = 0;
    public int ticksSinceLastViolation = 0;
    public double buffer = 0.0;
    public double damageMultiplier = 1.0;
    public double suspicion = 0.0;
    public int violationLevel = 0;
    public int ticksStep = 0;
    public static final int HISTORY_SIZE = 40;
    public final LinkedList<TickDataPOJO> movementHistory = new LinkedList<>();

    public PlayerProfile(float yaw, float pitch) {
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.aimProcessor = new AimProcessor();
    }

    public void addMovementFrame(TickDataPOJO frame) {
        this.movementHistory.addLast(frame);
        if (this.movementHistory.size() > HISTORY_SIZE) {
            this.movementHistory.removeFirst();
        }
    }

    public boolean isHistoryFull() {
        return this.movementHistory.size() == HISTORY_SIZE;
    }
}