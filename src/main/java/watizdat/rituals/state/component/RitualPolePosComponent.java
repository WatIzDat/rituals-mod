package watizdat.rituals.state.component;

import net.minecraft.world.World;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.state.component.abstraction.OptionalBlockPosComponent;

public class RitualPolePosComponent extends OptionalBlockPosComponent {
    public RitualPoleBlockEntity getBlockEntity(World world) {
        return (RitualPoleBlockEntity) world.getBlockEntity(value);
    }
}
