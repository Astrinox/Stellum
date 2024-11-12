package astrinox.stellum.handlers.explosion;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

import astrinox.stellum.handlers.screenshake.Screenshake;
import astrinox.stellum.handlers.screenshake.ScreenshakeHandler;
import astrinox.stellum.util.EasingHelper;
import astrinox.stellum.util.PerlinNoiseHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class ExplosionHandler {
    private BlockPos pos = new BlockPos(0, 0, 0);
    private int size = 16;
    private double noiseScale = 0.3;
    private float noiseMultiplier = 100;
    private boolean breakBlocks = true;
    private boolean hurtEntities = true;
    private float damage = 10;
    private boolean burnBlocks = false;
    private Burnmap burnMap;
    private int burnSize;
    private boolean burnDoFire;
    private Function<Double, Double> burnFalloffFunction = (Double x) -> EasingHelper.easeInSine(x);
    private boolean doScreenshake = true;
    private float screenshakeIntensity = 0.5f;
    private int screenshakeDurationMs = 1000;
    private boolean doScreenshakeFade = true;

    public ExplosionHandler setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public ExplosionHandler setSize(int size) {
        this.size = size;
        return this;
    }

    public ExplosionHandler setNoiseScale(double noiseScale) {
        this.noiseScale = noiseScale;
        return this;
    }

    public ExplosionHandler setNoiseMultiplier(float noiseMultiplier) {
        this.noiseMultiplier = noiseMultiplier;
        return this;
    }

    public ExplosionHandler setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
        return this;
    }

    public ExplosionHandler setHurtEntities(boolean hurtEntities) {
        this.hurtEntities = hurtEntities;
        return this;
    }

    public ExplosionHandler setDamage(float damage) {
        this.damage = damage;
        return this;
    }

    public ExplosionHandler setBurnBlocks(boolean burnBlocks) {
        this.burnBlocks = burnBlocks;
        return this;
    }

    public ExplosionHandler setBurnMap(Burnmap burnMap) {
        this.burnMap = burnMap;
        return this;
    }

    public ExplosionHandler setBurnSize(int burnSize) {
        this.burnSize = burnSize;
        return this;
    }

    public ExplosionHandler setBurnFalloffFunction(Function<Double, Double> burnFalloffFunction) {
        this.burnFalloffFunction = burnFalloffFunction;
        return this;
    }

    public ExplosionHandler setDoScreenshake(boolean doScreenshake) {
        this.doScreenshake = doScreenshake;
        return this;
    }

    public ExplosionHandler setScreenshakeIntensity(float screenshakeIntensity) {
        this.screenshakeIntensity = screenshakeIntensity;
        return this;
    }

    public ExplosionHandler setScreenshakeDurationMs(int screenshakeDurationMs) {
        this.screenshakeDurationMs = screenshakeDurationMs;
        return this;
    }

    public ExplosionHandler setDoScreenshakeFade(boolean doScreenshakeFade) {
        this.doScreenshakeFade = doScreenshakeFade;
        return this;
    }

    public ExplosionHandler setBurnDoFire(boolean burnDoFire) {
        this.burnDoFire = burnDoFire;
        return this;
    }

    public void trigger(World world) {
        int sizeSquared = size * size;
        Mutable blockPos = new Mutable();
        PerlinNoiseHelper noise = new PerlinNoiseHelper(new Random().nextLong(), noiseScale);

        for (int y = pos.getY() + size; y >= pos.getY() - size; y--) {
            for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
                for (int z = pos.getZ() - size; z <= pos.getZ() + size; z++) {
                    int dx = x - pos.getX();
                    int dy = y - pos.getY();
                    int dz = z - pos.getZ();

                    int distanceSquared = dx * dx + dy * dy + dz * dz;

                    if (distanceSquared <= sizeSquared + noise.noise(x, y, z) * noiseMultiplier) {
                        blockPos.set(x, y, z);
                        if (breakBlocks) {
                            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 0b01100010);
                        }
                        if (hurtEntities) {
                            Box box = new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
                            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box,
                                    e -> e.isAlive());
                            for (LivingEntity entity : entities) {
                                entity.damage(world.getDamageSources().generic(), damage);
                            }
                        }
                    }
                }
            }
        }

        if (burnBlocks) {
            BurnZone burnZone = new BurnZone()
                    .setPos(pos)
                    .setSize(burnSize)
                    .setExplosionSize(size)
                    .setNoiseScale(noiseScale)
                    .setNoiseMultiplier(noiseMultiplier)
                    .setBurnMap(burnMap)
                    .setFalloffFunction(burnFalloffFunction == null ? EasingHelper::easeInSine : burnFalloffFunction)
                    .setDoFire(burnDoFire);

            burnZone.trigger(world);
        }

        if (doScreenshake) {
            ScreenshakeHandler
                    .addScreenshake(new Screenshake(screenshakeIntensity, screenshakeDurationMs, doScreenshakeFade));
        }

    }
}
