package astrinox.stellum.handlers;

import net.minecraft.client.Camera;

public class ScreenshakeHandler {
    public static void updateCamera(Camera camera) {
        camera.setRotation(0, 0);
    }
}
