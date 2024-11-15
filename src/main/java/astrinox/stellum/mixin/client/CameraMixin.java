package astrinox.stellum.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astrinox.stellum.handlers.screenshake.ScreenshakeHandler;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "update", at = @At("RETURN"))
    public void screenshake(CallbackInfo ci) {
        ScreenshakeHandler.updateCamera((Camera) (Object) this);
    }
}
