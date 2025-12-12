package xyz.angames.anguardai.data;

public class TickDataPOJO {
    public float deltaYaw;
    public float deltaPitch;
    public float accelYaw;
    public float accelPitch;
    public float jerkYaw;
    public float jerkPitch;
    public float gcdErrorYaw;
    public float gcdErrorPitch;

    public TickDataPOJO(float deltaYaw, float deltaPitch, float accelYaw, float accelPitch, float jerkYaw, float jerkPitch, float gcdErrorYaw, float gcdErrorPitch) {
        this.deltaYaw = deltaYaw;
        this.deltaPitch = deltaPitch;
        this.accelYaw = accelYaw;
        this.accelPitch = accelPitch;
        this.jerkYaw = jerkYaw;
        this.jerkPitch = jerkPitch;
        this.gcdErrorYaw = gcdErrorYaw;
        this.gcdErrorPitch = gcdErrorPitch;
    }
}