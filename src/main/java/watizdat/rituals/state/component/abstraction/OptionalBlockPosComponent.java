package watizdat.rituals.state.component.abstraction;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public abstract class OptionalBlockPosComponent extends OptionalComponent<BlockPos> {
    @Override
    public void readFromNbt(NbtCompound tag) {
        if (!tag.contains("X")) {
            return;
        }

        value = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (value == null) {
            return;
        }

        tag.putInt("X", value.getX());
        tag.putInt("Y", value.getY());
        tag.putInt("Z", value.getZ());
    }
}
