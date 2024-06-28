package watizdat.rituals.init;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Vector3d;
import watizdat.rituals.state.ModPersistentState;
import watizdat.rituals.state.ModPlayerData;

import java.util.ArrayList;
import java.util.List;

public class ModEvents {
    public static List<Vector3d> positions = new ArrayList<>();
    public static long ticks;

    public static void init() {
        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
            if (entity.isPlayer()) {
                PlayerEntity player = (PlayerEntity) entity;

                player.sendMessage(Text.translatable("block.rituals.bed.message"), true);
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if (entity.isPlayer() && !entity.getWorld().isClient && ((PlayerEntity) entity).canResetTimeBySleeping()) {
                ModPlayerData playerState = ModPersistentState.getPlayerState(entity);

                playerState.entityTypesKilled.clear();
            }
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            ModPersistentState state = ModPersistentState.getServerState(server);

            positions = new ArrayList<>(state.particlePositions);

            ticks = 20;
        });
    }
}
