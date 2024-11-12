package astrinox.stellum.handlers.explosion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Property;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Burnmap {
    public final List<BurnmapEntry> BURNMAP;

    public record BurnmapEntry(List<Codecs.TagEntryId> source, List<Codecs.TagEntryId> replace) {
    }

    public Burnmap(List<BurnmapEntry> BURNMAP) {
        this.BURNMAP = BURNMAP;
    }

    private <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
            Comparable<?> value) {
        return state.with(property, property.getType().cast(value));
    }

    public BlockState getOutput(BlockState block) {
        BlockState outputBlock = block;
        for (BurnmapEntry entry : BURNMAP) {
            ArrayList<BlockState> replacements = new ArrayList<>();
            for (Codecs.TagEntryId sourceId : entry.source) {
                for (Codecs.TagEntryId replaceId : entry.replace) {
                    boolean isSourceTag = sourceId.comp_814();
                    boolean isReplaceTag = replaceId.comp_814();

                    if (isReplaceTag) {
                        TagKey<Block> tag = TagKey.of(RegistryKeys.BLOCK, replaceId.comp_813());
                        Registries.BLOCK.iterateEntries(tag)
                                .forEach((b) -> replacements.add(b.comp_349().getDefaultState()));
                    } else {
                        replacements.add(Registries.BLOCK.get(replaceId.comp_813()).getDefaultState());
                    }

                    if (isSourceTag) {
                        TagKey<Block> sourceTag = TagKey.of(RegistryKeys.BLOCK, sourceId.comp_813());
                        if (block.isIn(sourceTag)) {
                            outputBlock = replacements.get(new Random().nextInt(replacements.size()));
                        }
                    } else {
                        if (Registries.BLOCK.getId(block.getBlock()).equals(sourceId.comp_813())) {
                            outputBlock = replacements.get(new Random().nextInt(replacements.size()));
                        }
                    }
                }
            }
        }

        for (Property<?> property : block.getProperties()) {
            if (outputBlock.getProperties().contains(property)) {
                outputBlock = applyProperty(outputBlock, property, block.get(property));
            }
        }

        return outputBlock;
    }

    public static final Codec<BurnmapEntry> BURNMAP_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.TAG_ENTRY_ID.listOf().fieldOf("source").forGetter(BurnmapEntry::source),
            Codecs.TAG_ENTRY_ID.listOf().fieldOf("replace").forGetter(BurnmapEntry::replace))
            .apply(instance, BurnmapEntry::new));

    public static final Codec<Burnmap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BURNMAP_CODEC.listOf().fieldOf("burnmap").forGetter(burnmap -> burnmap.BURNMAP))
            .apply(instance, Burnmap::new));
}
