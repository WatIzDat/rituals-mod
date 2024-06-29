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
import org.joml.Vector3d;
import watizdat.rituals.Rituals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ModPersistentState extends PersistentState {
    public HashMap<UUID, ModPlayerData> players = new HashMap<>();
//    public List<Vector3d> particlePositions = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
//        NbtCompound particlePositionsNbt = new NbtCompound();
//
//        for (int i = 0; i < particlePositions.size(); i++) {
//            Vector3d position = particlePositions.get(i);
//
//            NbtCompound vector3dNbt = new NbtCompound();
//
//            vector3dNbt.putDouble("x", position.x);
//            vector3dNbt.putDouble("y", position.y);
//            vector3dNbt.putDouble("z", position.z);
//
//            particlePositionsNbt.put("particlePositions" + i, vector3dNbt);
//        }
//
//        nbt.put("particlePositions", particlePositionsNbt);

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

//        NbtCompound particlePositionsNbt = tag.getCompound("particlePositions");
//
//        particlePositionsNbt.getKeys().forEach(particlePositionsKey -> {
//            NbtCompound vector3dNbt = particlePositionsNbt.getCompound(particlePositionsKey);
//
//            Vector3d vector3d = new Vector3d();
//
//            vector3d.x = vector3dNbt.getDouble("x");
//            vector3d.y = vector3dNbt.getDouble("y");
//            vector3d.z = vector3dNbt.getDouble("z");
//
//            state.particlePositions.add(vector3d);
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
