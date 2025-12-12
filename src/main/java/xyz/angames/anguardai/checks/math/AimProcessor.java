package xyz.angames.anguardai.checks.math;

import xyz.angames.anguardai.utils.MathUtil;
import xyz.angames.anguardai.utils.Pair;
import xyz.angames.anguardai.utils.RunningMode;

public class AimProcessor {

    private static final int SIGNIFICANT_SAMPLES_THRESHOLD = 15;
    private static final int TOTAL_SAMPLES_THRESHOLD = 80;
    public double sensitivityX, sensitivityY;
    public double divisorX, divisorY;
    public double modeX, modeY;
    public double deltaDotsX, deltaDotsY;
    private final RunningMode xRotMode = new RunningMode(TOTAL_SAMPLES_THRESHOLD);
    private final RunningMode yRotMode = new RunningMode(TOTAL_SAMPLES_THRESHOLD);
    private float lastXRot, lastYRot;
    private float lastDeltaYaw, lastDeltaPitch;
    public float accelYaw, accelPitch;
    public float jerkYaw, jerkPitch;
    public float currentYawAccel, currentPitchAccel;
    private float lastAccelYaw, lastAccelPitch;

    public void process(float deltaYaw, float deltaPitch) {
        float deltaYawAbs = Math.abs(deltaYaw);
        float deltaPitchAbs = Math.abs(deltaPitch);
        this.currentYawAccel = deltaYawAbs - Math.abs(this.lastDeltaYaw);
        this.currentPitchAccel = deltaPitchAbs - Math.abs(this.lastDeltaPitch);
        this.jerkYaw = this.currentYawAccel - this.lastAccelYaw;
        this.jerkPitch = this.currentPitchAccel - this.lastAccelPitch;
        this.accelYaw = this.currentYawAccel;
        this.accelPitch = this.currentPitchAccel;
        this.lastAccelYaw = this.currentYawAccel;
        this.lastAccelPitch = this.currentPitchAccel;
        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
        this.divisorX = MathUtil.gcd(deltaYawAbs, lastXRot);
        if (deltaYawAbs > 0 && deltaYawAbs < 5 && divisorX > MathUtil.MINIMUM_DIVISOR) {
            this.xRotMode.add(divisorX);
            this.lastXRot = deltaYawAbs;
        }
        this.divisorY = MathUtil.gcd(deltaPitchAbs, lastYRot);
        if (deltaPitchAbs > 0 && deltaPitchAbs < 5 && divisorY > MathUtil.MINIMUM_DIVISOR) {
            this.yRotMode.add(divisorY);
            this.lastYRot = deltaPitchAbs;
        }

        updateModeX();
        updateModeY();
        if (modeX > 0) this.deltaDotsX = deltaYawAbs / modeX;
        if (modeY > 0) this.deltaDotsY = deltaPitchAbs / modeY;
    }


    public void reset() {
        this.currentYawAccel = 0;
        this.currentPitchAccel = 0;
        this.accelYaw = 0;
        this.accelPitch = 0;
        this.jerkYaw = 0;
        this.jerkPitch = 0;
        this.lastAccelYaw = 0;
        this.lastAccelPitch = 0;
        this.lastDeltaYaw = 0;
        this.lastDeltaPitch = 0;
    }

    private void updateModeX() {
        if (this.xRotMode.size() > SIGNIFICANT_SAMPLES_THRESHOLD) {
            Pair<Double, Integer> result = this.xRotMode.getMode();
            if (result.second() > SIGNIFICANT_SAMPLES_THRESHOLD) {
                this.modeX = result.first();
                this.sensitivityX = convertToSensitivity(this.modeX);
            }
        }
    }

    private void updateModeY() {
        if (this.yRotMode.size() > SIGNIFICANT_SAMPLES_THRESHOLD) {
            Pair<Double, Integer> result = this.yRotMode.getMode();
            if (result.second() > SIGNIFICANT_SAMPLES_THRESHOLD) {
                this.modeY = result.first();
                this.sensitivityY = convertToSensitivity(this.modeY);
            }
        }
    }

    public float getGcdErrorYaw(float delta) {
        if (modeX <= 1e-5) return 0.0f;
        double modulo = Math.abs(delta) % modeX;
        return (float) Math.min(modulo, modeX - modulo);
    }

    public float getGcdErrorPitch(float delta) {
        if (modeY <= 1e-5) return 0.0f;
        double modulo = Math.abs(delta) % modeY;
        return (float) Math.min(modulo, modeY - modulo);
    }

    public static double convertToSensitivity(double gcd) {
        double var11 = gcd / 0.15F / 8.0D;
        double var9 = Math.cbrt(var11);
        return (var9 - 0.2f) / 0.6f;
    }
}