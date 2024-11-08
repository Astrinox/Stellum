package astrinox.stellum.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astrinox.stellum.Stellum;
import net.minecraft.client.Camera;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "setup", at = @At("RETURN"))
    public void screenshake(CallbackInfo ci) {
        Stellum.LOGGER.info("it fucking worked lets gooo");
    }
}
