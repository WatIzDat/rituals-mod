package watizdat.rituals.state.component;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import watizdat.rituals.state.component.abstraction.ArrayListComponent;

public class EntityTypesKilledComponent extends ArrayListComponent<EntityType<?>> {
    @Override
    public EntityType<?> readSingleElementFromNbt(NbtCompound tag, String keyOfElement) {
        return Registries.ENTITY_TYPE.get(new Identifier(tag.getString(keyOfElement)));
    }

    @Override
    public void writeSingleElementToNbt(NbtCompound tag, EntityType<?> e, int indexOfElement) {
        tag.putString(String.valueOf(indexOfElement), EntityType.getId(e).toString());
    }
}
