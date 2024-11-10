package astrinox.stellum.handlers.explosion;

import java.util.Random;

import net.minecraft.block.BlockState;

public class BurnableIO {
    private Burnable in;
    private Burnable out;

    public BurnableIO(Burnable in, Burnable out) {
        this.in = in;
        this.out = out;
    }

    public BlockState getOutput(BlockState state) {
        if (in.getPossibleBlockStates().contains(state)) {
            return out.getPossibleBlockStates().get(new Random().nextInt(out.getPossibleBlockStates().size()));
        }
        return state;
    }

    public Burnable getIn() {
        return in;
    }

    public Burnable getOut() {
        return out;
    }
}
