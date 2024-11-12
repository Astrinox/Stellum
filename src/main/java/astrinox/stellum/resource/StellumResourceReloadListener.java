package astrinox.stellum.resource;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import astrinox.stellum.Stellum;
import astrinox.stellum.handlers.explosion.Burnmap;
import astrinox.stellum.registry.BurnMapRegistry;

public class StellumResourceReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Stellum.MOD_ID, "stellum_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        Stellum.LOGGER.info("Reloading resources");
        for (Identifier id : manager.findResources("burnmap", path -> path.getPath().endsWith(".json")).keySet()) {
            try (InputStream stream = manager.getResourceOrThrow(id).getInputStream()) {
                Stellum.LOGGER.info("Loaded burnmap: " + id.toString());
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(new String(stream.readAllBytes()), JsonObject.class);

                Burnmap burnmap = Burnmap.CODEC.parse(JsonOps.INSTANCE, jsonObject).resultOrPartial().orElse(null);

                if (burnmap != null) {
                    BurnMapRegistry
                            .registerBurnMap(
                                    Identifier.of(Stellum.MOD_ID,
                                            id.getPath().substring(id.getPath().lastIndexOf('/') + 1).replace(".json",
                                                    "")),
                                    burnmap);
                }
            } catch (Exception e) {
                Stellum.LOGGER.error("Error occurred while loading resource JSON: " + id.toString(), e);
            }
        }
    }

}