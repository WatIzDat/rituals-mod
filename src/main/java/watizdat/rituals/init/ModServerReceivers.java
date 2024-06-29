package watizdat.rituals.init;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.network.ModNetworkConstants;

public class ModServerReceivers {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ModNetworkConstants.START_RITUAL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                ((RitualPoleBlockEntity) player.getWorld().getBlockEntity(pos)).startRitual(player);
            });
        });
    }
}
