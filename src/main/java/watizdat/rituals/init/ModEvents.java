package watizdat.rituals.init;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import watizdat.rituals.state.ModPersistentState;
import watizdat.rituals.state.ModPlayerData;

public class ModEvents {
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
    }
}
