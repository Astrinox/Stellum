package astrinox.stellum.handlers.screenshake;

import java.util.ArrayList;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec2f;
import astrinox.stellum.util.EasingHelper;
import astrinox.stellum.util.MathHelper;

public class ScreenshakeHandler {
    public static ArrayList<Screenshake> screenshakes = new ArrayList<>();

    public static void updateCamera(Camera camera) {
        ArrayList<Screenshake> toRemove = new ArrayList<>();
        for (Screenshake screenshake : screenshakes) {
            if (System.currentTimeMillis() > screenshake.endTime) {
                toRemove.add(screenshake);
            }
        }
        for (Screenshake screenshake : toRemove) {
            screenshakes.remove(screenshake);
        }

        Vec2f shake = getShake();
        camera.setRotation(camera.getYaw() + shake.y, camera.getPitch() + shake.x);
    }

    public static void addScreenshake(Screenshake screenshake) {
        screenshakes.add(screenshake);
        screenshake.startTime = System.currentTimeMillis();
        screenshake.endTime = (long) (screenshake.startTime + screenshake.durationMs);
    }

    private static Vec2f getShake() {
        double time = System.currentTimeMillis() / 10;
        double resultX = Math.sin(2 * time) + 0.7 * Math.sin(Math.PI * (time + 0.5)) / 2;
        double resultY = Math.sin(2 * (time + 0.75)) + 0.8 * Math.sin(Math.PI * (time + 0.25)) / 2;

        double sum = 0;
        for (Screenshake screenshake : screenshakes) {
            if (screenshake.fade) {
                sum += EasingHelper.easeInQuad(
                        MathHelper.map(System.currentTimeMillis(), screenshake.startTime, screenshake.endTime,
                                screenshake.intensity, 0));
            } else {
                sum += screenshake.intensity;
            }
        }

        if (sum == 0) {
            return new Vec2f(0, 0);
        }
        return new Vec2f((float) (resultX * sum), (float) (resultY * sum));
    }
}
