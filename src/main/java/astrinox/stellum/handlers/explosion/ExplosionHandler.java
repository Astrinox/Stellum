package astrinox.stellum.handlers.explosion;

import java.util.Random;

import astrinox.stellum.util.PerlinNoiseHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class ExplosionHandler {
    private BlockPos pos;
    private int size;
    private double noiseScale;
    private float noiseMultiplier;
    private ScorchZone scorchZone;
    private boolean breakBlocks;
    private boolean hurtEntities;

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

    public ExplosionHandler setScorchZone(ScorchZone scorchZone) {
        this.scorchZone = scorchZone;
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

    public void trigger(World world) {
        int sizeSquared = size * size;
        Mutable blockPos = new Mutable();
        PerlinNoiseHelper noise = new PerlinNoiseHelper(new Random().nextLong(), noiseScale);

        for (int x = pos.getX() - size; x <= pos.getX() + size; x++) {
            for (int y = pos.getY() - size; y <= pos.getY() + size; y++) {
                for (int z = pos.getZ() - size; z <= pos.getZ() + size; z++) {
                    int dx = x - pos.getX();
                    int dy = y - pos.getY();
                    int dz = z - pos.getZ();

                    int distanceSquared = dx * dx + dy * dy + dz * dz;

                    if (distanceSquared <= sizeSquared + noise.noise(x, y, z) * noiseMultiplier) {
                        blockPos.set(x, y, z);
                        world.removeBlock(blockPos, false);
                    }
                }
            }
        }
    }
}
