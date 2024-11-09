package astrinox.stellum.handlers.screenshake;

import java.util.ArrayList;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec2;

import astrinox.stellum.util.MathHelper;

public class ScreenshakeHandler {
    public static ArrayList<Screenshake> screenshakes = new ArrayList<>();

    public void updateCamera(Camera camera) {
        ArrayList<Screenshake> toRemove = new ArrayList<>();
        for (Screenshake screenshake : screenshakes) {
            if (System.currentTimeMillis() > screenshake.endTime) {
                toRemove.add(screenshake);
            }
        }
        for (Screenshake screenshake : toRemove) {
            screenshakes.remove(screenshake);
        }

        Vec2 shake = getShake();
        camera.setRotation(shake.x, shake.y);
    }

    public void addScreenshake(Screenshake screenshake) {
        screenshakes.add(screenshake);
        screenshake.startTime = System.currentTimeMillis();
        screenshake.endTime = (long) (screenshake.startTime + screenshake.durationMs);
    }

    private static Vec2 getShake() {
        double time = System.currentTimeMillis() / 500;
        double resultX = Math.sin(2 * time) + Math.sin(Math.PI * time);
        double resultY = Math.sin(2 * time + 8687487) + Math.sin(Math.PI * time + 8687487);

        double sum = 0;
        for (Screenshake screenshake : screenshakes) {
            if (screenshake.fade) {
                sum += MathHelper.map(System.currentTimeMillis(), screenshake.startTime, screenshake.endTime,
                        screenshake.intensity, 0);
            } else {
                sum += screenshake.intensity;
            }
        }

        if (sum == 0) {
            return new Vec2(0, 0);
        }
        return new Vec2((float) (resultX * sum), (float) (resultY * sum));
    }
}
