package watizdat.rituals.state;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.entity.LivingEntity;
import watizdat.rituals.Rituals;
import watizdat.rituals.state.component.EntityTypesKilledComponent;
import watizdat.rituals.state.component.RitualPolePosComponent;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<EntityTypesKilledComponent> ENTITY_TYPES_KILLED_COMPONENT =
            ComponentRegistry.getOrCreate(Rituals.id("entity_types_killed"), EntityTypesKilledComponent.class);

    public static final ComponentKey<RitualPolePosComponent> RITUAL_POLE_POS_COMPONENT =
            ComponentRegistry.getOrCreate(Rituals.id("ritual_pole_pos"), RitualPolePosComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(ENTITY_TYPES_KILLED_COMPONENT, player -> new EntityTypesKilledComponent(), RespawnCopyStrategy.LOSSLESS_ONLY);

        registry.registerFor(LivingEntity.class, RITUAL_POLE_POS_COMPONENT, entity -> new RitualPolePosComponent());
    }
}
