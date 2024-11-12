package astrinox.stellum.handlers.explosion;

import java.util.Random;
import java.util.function.Function;

import org.joml.Math;

import astrinox.stellum.util.EasingHelper;
import astrinox.stellum.util.MathHelper;
import astrinox.stellum.util.PerlinNoiseHelper;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class BurnZone {
    private BlockPos pos = new BlockPos(0, 0, 0);
    private int size = 10;
    private int explosionSize = 16;
    private double noiseScale = 0.3;
    private float noiseMultiplier = 100;
    private Burnmap burnMap;
    private Function<Double, Double> falloffFunction = EasingHelper::easeInSine;
    private boolean doFire = true;

    public BurnZone setPos(BlockPos pos) {
        this.pos = pos;
        return this;
    }

    public BurnZone setSize(int size) {
        this.size = size;
        return this;
    }

    public BurnZone setExplosionSize(int explosionSize) {
        this.explosionSize = explosionSize;
        return this;
    }

    public BurnZone setNoiseScale(double noiseScale) {
        this.noiseScale = noiseScale;
        return this;
    }

    public BurnZone setNoiseMultiplier(float noiseMultiplier) {
        this.noiseMultiplier = noiseMultiplier;
        return this;
    }

    public BurnZone setBurnMap(Burnmap burnMap) {
        this.burnMap = burnMap;
        return this;
    }

    public BurnZone setFalloffFunction(Function<Double, Double> falloffFunction) {
        this.falloffFunction = falloffFunction;
        return this;
    }

    public BurnZone setDoFire(boolean doFire) {
        this.doFire = doFire;
        return this;
    }

    public void trigger(World world) {
        int trueSize = size + explosionSize;
        int trueSizeSquared = trueSize * trueSize;
        int explosionSizeSquared = explosionSize * explosionSize;
        Mutable blockPos = new Mutable();
        PerlinNoiseHelper noise = new PerlinNoiseHelper(new Random().nextLong(), noiseScale);

        for (int y = pos.getY() + trueSize; y >= pos.getY() - trueSize; y--) {
            for (int x = pos.getX() - trueSize; x <= pos.getX() + trueSize; x++) {
                for (int z = pos.getZ() - trueSize; z <= pos.getZ() + trueSize; z++) {
                    int dx = x - pos.getX();
                    int dy = y - pos.getY();
                    int dz = z - pos.getZ();

                    int distanceSquared = dx * dx + dy * dy + dz * dz;

                    double noisePoint = noise.noise(x, y, z) * noiseMultiplier;

                    if (distanceSquared <= trueSizeSquared + noisePoint) {
                        blockPos.set(x, y, z);
                        if (distanceSquared >= explosionSizeSquared + noisePoint) {
                            double percent = MathHelper.map(distanceSquared, explosionSizeSquared, trueSizeSquared, 0,
                                    1);
                            if (falloffFunction != null) {
                                percent = falloffFunction.apply(percent);
                            }
                            if (Math.random() < percent) {
                            } else if (burnMap != null) {
                                world.setBlockState(blockPos, burnMap.getOutput(world.getBlockState(blockPos)),
                                        0b01100010);
                                if (world.getBlockState(blockPos.down()).isSolidBlock(world, blockPos)
                                        && world.getBlockState(blockPos.up()).isAir()
                                        && Math.random() < 0.05 && doFire) {
                                    world.setBlockState(blockPos, Blocks.FIRE.getDefaultState(), 0b01100010);
                                }
                            }
                        } else {
                            if (burnMap != null) {
                                world.setBlockState(blockPos, burnMap.getOutput(world.getBlockState(blockPos)),
                                        0b01100010);
                                if (world.getBlockState(blockPos.down()).isSolidBlock(world, blockPos)
                                        && world.getBlockState(blockPos.up()).isAir()
                                        && Math.random() < 0.05 && doFire) {
                                    world.setBlockState(blockPos, Blocks.FIRE.getDefaultState(), 0b01100010);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}