package watizdat.rituals.init;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import watizdat.rituals.Rituals;

public class ModTags {
    public static final TagKey<Block> NETHER_RITUAL_POLE_NOT_PLACEABLE =
            TagKey.of(RegistryKeys.BLOCK, Rituals.id("nether_ritual_pole_not_placeable"));

    public static final TagKey<EntityType<?>> LIVES_IN_OVERWORLD_WATER =
            TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "lives_in_overworld_water"));

    public static final TagKey<EntityType<?>> LIVES_IN_OVERWORLD_LAND =
            TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "lives_in_overworld_land"));

    public static final TagKey<EntityType<?>> LIVES_IN_OVERWORLD_FLYING =
            TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "lives_in_overworld_flying"));

    public static final TagKey<EntityType<?>> LIVES_IN_NETHER =
            TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "lives_in_nether"));

    public static final TagKey<EntityType<?>> LIVES_IN_END =
            TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("c", "lives_in_end"));
}
