package watizdat.rituals.state;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import watizdat.rituals.Rituals;

import java.util.ArrayList;
import java.util.List;

public class ModPersistentState extends PersistentState {
    public List<EntityType<?>> entityTypesKilled = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound entityTypesKilledNbt = new NbtCompound();

        for (int i = 0; i < entityTypesKilled.size(); i++) {
            EntityType<?> entityType = entityTypesKilled.get(i);

            entityTypesKilledNbt.putString("entityTypesKilled" + i, EntityType.getId(entityType).toString());
        }

        nbt.put("entityTypesKilled", entityTypesKilledNbt);

        return nbt;
    }

    public static ModPersistentState createFromNbt(NbtCompound tag) {
        ModPersistentState state = new ModPersistentState();

        NbtCompound entityTypesKilledNbt = tag.getCompound("entityTypesKilled");
        entityTypesKilledNbt.getKeys().forEach(key -> {
            state.entityTypesKilled.add(Registries.ENTITY_TYPE.get(new Identifier(entityTypesKilledNbt.getString(key))));
        });

        return state;
    }

    public static ModPersistentState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        ModPersistentState state = persistentStateManager.getOrCreate(
                ModPersistentState::createFromNbt,
                ModPersistentState::new,
                Rituals.MOD_ID);

        state.markDirty();

        return state;
    }
}
