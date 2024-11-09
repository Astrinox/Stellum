package astrinox.stellum.handlers.screenshake;

public class Screenshake {
    float intensity;
    long startTime;
    double durationMs;
    long endTime;
    boolean fade;

    public Screenshake(float intensity, int durationMs, boolean fade) {
        this.intensity = intensity;
        this.durationMs = durationMs;
        this.fade = fade;
    }
}
