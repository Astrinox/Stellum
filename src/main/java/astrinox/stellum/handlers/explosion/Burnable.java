package astrinox.stellum.handlers.explosion;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

public class Burnable {
    private ArrayList<BlockState> blocks = new ArrayList<>();

    public Burnable addBlock(BlockState block) {
        blocks.add(block);
        return this;
    }

    public Burnable addTag(TagKey<Block> tag) {
        Registries.BLOCK.iterateEntries(tag).forEach((block) -> blocks.add(block.comp_349().getDefaultState()));
        return this;
    }

    public ArrayList<BlockState> getPossibleBlockStates() {
        return blocks;
    }
}
