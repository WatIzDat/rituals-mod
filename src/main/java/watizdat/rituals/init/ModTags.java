package watizdat.rituals.init;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import watizdat.rituals.Rituals;

public class ModTags {
    public static final TagKey<Block> NETHER_RITUAL_POLE_NOT_PLACEABLE =
            TagKey.of(RegistryKeys.BLOCK, Rituals.id("nether_ritual_pole_not_placeable"));

    public static final TagKey<EntityType<?>> AQUATIC =
            TagKey.of(RegistryKeys.ENTITY_TYPE, Rituals.id("aquatic"));
}
