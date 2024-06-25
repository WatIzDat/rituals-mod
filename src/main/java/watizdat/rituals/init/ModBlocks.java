package watizdat.rituals.init;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import watizdat.rituals.Rituals;
import watizdat.rituals.block.RitualPoleBlock;

public class ModBlocks {
    public static final Block RITUAL_POLE = new RitualPoleBlock(FabricBlockSettings.create().strength(-1.0F, 3600000.0F));

    public static void init() {
        Registry.register(Registries.BLOCK, Rituals.id("ritual_pole"), RITUAL_POLE);
    }
}
