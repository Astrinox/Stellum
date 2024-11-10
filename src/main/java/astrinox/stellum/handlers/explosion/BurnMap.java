package astrinox.stellum.handlers.explosion;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

public class BurnMap {
    ArrayList<BurnableIO> burnMap = new ArrayList<>();

    public BurnMap addBurnables(BurnableIO burnable) {
        burnMap.add(burnable);
        return this;
    }

    public ArrayList<BurnableIO> getBurnMap() {
        return burnMap;
    }

    private <T extends Comparable<T>> BlockState applyProperty(BlockState state, Property<T> property,
            Comparable<?> value) {
        return state.with(property, property.getType().cast(value));
    }

    public BlockState getOutput(BlockState state) {
        BlockState outputState = state;
        for (BurnableIO burnable : burnMap) {
            if (burnable.getIn().getPossibleBlockStates().contains(state.getBlock().getDefaultState())) {
                outputState = burnable.getOut().getPossibleBlockStates()
                        .get(new Random().nextInt(burnable.getOut().getPossibleBlockStates().size()));

                for (Property<?> property : state.getProperties()) {
                    if (outputState.getProperties().contains(property)) {
                        outputState = applyProperty(outputState, property, state.get(property));
                    }
                }
                return outputState;
            }
        }
        return outputState;
    }
}
