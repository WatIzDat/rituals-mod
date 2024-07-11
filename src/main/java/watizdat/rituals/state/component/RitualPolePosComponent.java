package watizdat.rituals.state.component;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.state.component.abstraction.OptionalBlockPosComponent;

public class RitualPolePosComponent extends OptionalBlockPosComponent {
    @Override
    public void set(@NotNull BlockPos value) {
        super.set(value);
    }

    public RitualPoleBlockEntity getBlockEntity(World world) {
        return (RitualPoleBlockEntity) world.getBlockEntity(value);
    }
}
