package astrinox.stellum.registry;

import java.util.HashMap;
import java.util.Map;

import astrinox.stellum.Stellum;
import astrinox.stellum.handlers.explosion.Burnmap;
import net.minecraft.util.Identifier;

public class BurnMapRegistry {
    private static final Map<Identifier, Burnmap> burnMapRegistry = new HashMap<>();

    public static void registerBurnMap(Identifier id, Burnmap burnMap) {
        burnMapRegistry.put(id, burnMap);
        Stellum.LOGGER.info("Registered burnmap: " + id);
    }

    public static Burnmap getBurnMap(Identifier id) {
        return burnMapRegistry.get(id);
    }

    public static void clear() {
        burnMapRegistry.clear();
    }
}