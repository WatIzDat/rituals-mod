package watizdat.rituals.state;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import watizdat.rituals.Rituals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ModPersistentState extends PersistentState {
    public HashMap<UUID, ModPlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
//        NbtCompound entityTypesKilledNbt = new NbtCompound();
//
//        for (int i = 0; i < entityTypesKilled.size(); i++) {
//            EntityType<?> entityType = entityTypesKilled.get(i);
//
//            entityTypesKilledNbt.putString("entityTypesKilled" + i, EntityType.getId(entityType).toString());
//        }
//
//        nbt.put("entityTypesKilled", entityTypesKilledNbt);

        NbtCompound playersNbt = new NbtCompound();

        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            NbtCompound entityTypesKilledNbt = new NbtCompound();

            for (int i = 0; i < playerData.entityTypesKilled.size(); i++) {
                EntityType<?> entityType = playerData.entityTypesKilled.get(i);

                entityTypesKilledNbt.putString("entityTypesKilled" + i, EntityType.getId(entityType).toString());
            }

            playerNbt.put("entityTypesKilled", entityTypesKilledNbt);

            playersNbt.put(uuid.toString(), playerNbt);
        });

        nbt.put("players", playersNbt);

        return nbt;
    }

    public static ModPersistentState createFromNbt(NbtCompound tag) {
        ModPersistentState state = new ModPersistentState();
//
//        NbtCompound entityTypesKilledNbt = tag.getCompound("entityTypesKilled");
//        entityTypesKilledNbt.getKeys().forEach(key -> {
//            state.entityTypesKilled.add(Registries.ENTITY_TYPE.get(new Identifier(entityTypesKilledNbt.getString(key))));
//        });

        NbtCompound playersNbt = tag.getCompound("players");

        playersNbt.getKeys().forEach(playersKey -> {
            ModPlayerData playerData = new ModPlayerData();

            NbtCompound entityTypesKilledNbt = playersNbt.getCompound(playersKey).getCompound("entityTypesKilled");

            entityTypesKilledNbt.getKeys().forEach(entityTypesKey -> {
                playerData.entityTypesKilled.add(Registries.ENTITY_TYPE.get(new Identifier(entityTypesKilledNbt.getString(entityTypesKey))));
            });

            UUID uuid = UUID.fromString(playersKey);
            state.players.put(uuid, playerData);
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

    public static ModPlayerData getPlayerState(LivingEntity player) {
        ModPersistentState serverState = getServerState(player.getWorld().getServer());

        ModPlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new ModPlayerData());

        return playerState;
    }
}
