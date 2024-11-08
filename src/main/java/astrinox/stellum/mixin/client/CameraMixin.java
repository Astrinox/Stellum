package astrinox.stellum.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astrinox.stellum.Stellum;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public abstract interface CameraMixin {
    @Invoker("setRotation")
    public abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    public static void cameraUpdate(CallbackInfo ci) {
        Stellum.LOGGER.info("camera mixin worked");
    }
}
