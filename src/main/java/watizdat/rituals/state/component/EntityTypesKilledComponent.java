package watizdat.rituals.state.component;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import watizdat.rituals.state.component.abstraction.HashMapComponent;

import java.util.Map;

public class EntityTypesKilledComponent extends HashMapComponent<EntityType<?>, Integer> {
    @Override
    public Map.Entry<EntityType<?>, Integer> readSingleEntryFromNbt(NbtCompound tag, String keyOfEntry) {
        return Map.entry(Registries.ENTITY_TYPE.get(new Identifier(keyOfEntry)), tag.getInt(keyOfEntry));
    }

    @Override
    public void writeSingleEntryToNbt(NbtCompound tag, Map.Entry<EntityType<?>, Integer> entry) {
        tag.putInt(EntityType.getId(entry.getKey()).toString(), entry.getValue());
    }

    @Override
    public void put(EntityType<?> key, Integer value) {
        if (value <= 0) {
            remove(key);
        } else {
            super.put(key, value);
        }
    }

    public void addEntityType(EntityType<?> entityType) {
        put(entityType, getMap().containsKey(entityType) ? getMap().get(entityType) + 1 : 1);
    }
}
