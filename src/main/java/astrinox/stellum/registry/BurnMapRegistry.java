package astrinox.stellum.registry;

import java.util.HashMap;
import java.util.Map;

import astrinox.stellum.Stellum;
import astrinox.stellum.handlers.explosion.BurnMap;

public class BurnMapRegistry {
    private static final Map<String, BurnMap> burnMapRegistry = new HashMap<>();

    public static void registerBurnMap(String id, BurnMap burnMap) {
        burnMapRegistry.put(id, burnMap);
        Stellum.LOGGER.info("Registered burnmap: " + id);
    }

    public static BurnMap getBurnMap(String id) {
        return burnMapRegistry.get(id);
    }

    public static void clear() {
        burnMapRegistry.clear();
    }
}