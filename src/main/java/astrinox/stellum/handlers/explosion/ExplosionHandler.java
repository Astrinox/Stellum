package astrinox.stellum.handlers.explosion;

import java.util.List;
import java.util.Random;

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
    private boolean effectsPass = true;
    private int effectsSizeDifference = 6;
    private boolean scorchBlocks = true;
    private boolean doFire = true;
    private boolean breakBlocks = true;
    private boolean hurtEntities = true;
    private float damage = 10;

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

    public ExplosionHandler setEffectsPass(boolean doEffectsPass) {
        this.effectsPass = doEffectsPass;
        return this;
    }

    public ExplosionHandler setEffectsSizeDifference(int effectsSizeDifference) {
        this.effectsSizeDifference = effectsSizeDifference;
        return this;
    }

    public ExplosionHandler setScorchBlocks(boolean scorchBlocks) {
        this.scorchBlocks = scorchBlocks;
        return this;
    }

    public ExplosionHandler setDoFire(boolean doFire) {
        this.doFire = doFire;
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

    public void trigger(World world) {
        int sizeSquared = size * size;
        Mutable blockPos = new Mutable();
        PerlinNoiseHelper mainNoise = new PerlinNoiseHelper(new Random().nextLong(), noiseScale);

        for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
            for (int y = pos.getY() - size; y <= pos.getY() + size; y++) {
                for (int z = pos.getZ() - size; z <= pos.getZ() + size; z++) {
                    int dx = x - pos.getX();
                    int dy = y - pos.getY();
                    int dz = z - pos.getZ();

                    int distanceSquared = dx * dx + dy * dy + dz * dz;

                    if (distanceSquared <= sizeSquared + mainNoise.noise(x, y, z) * noiseMultiplier) {
                        blockPos.set(x, y, z);
                        if (breakBlocks) {
                            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
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

        if (!effectsPass) {
            return;
        }

        PerlinNoiseHelper effectsNoise = new PerlinNoiseHelper(new Random().nextLong(), noiseScale);

        int effectsSize = size + effectsSizeDifference;
        int effectsSizeSquared = effectsSize * effectsSize;
        for (int x = pos.getX() - effectsSize; x <= pos.getX() + effectsSize; x++) {
            for (int y = pos.getY() - effectsSize; y <= pos.getY() + effectsSize; y++) {
                for (int z = pos.getZ() - effectsSize; z <= pos.getZ() + effectsSize; z++) {
                    int dx = x - pos.getX();
                    int dy = y - pos.getY();
                    int dz = z - pos.getZ();

                    int distanceSquared = dx * dx + dy * dy + dz * dz;

                    if (distanceSquared <= effectsSizeSquared + effectsNoise.noise(x, y, z) * noiseMultiplier) {
                        blockPos.set(x, y, z);
                        if (doFire) {
                            if (world.getBlockState(blockPos).isAir()
                                    && world.getBlockState(blockPos.down()).isSolidBlock(world, blockPos)
                                    && Math.random() < 0.1) {
                                world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }
}
