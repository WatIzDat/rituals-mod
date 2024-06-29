package watizdat.rituals.init;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import watizdat.rituals.Rituals;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;

public class ModBlockEntityTypes {
    public static final BlockEntityType<RitualPoleBlockEntity> RITUAL_POLE = FabricBlockEntityTypeBuilder.create(
            RitualPoleBlockEntity::new,
            ModBlocks.RITUAL_POLE
    ).build();

    public static void init() {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, Rituals.id("ritual_pole"), RITUAL_POLE);
    }
}
