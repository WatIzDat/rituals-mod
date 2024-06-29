package watizdat.rituals.init;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.state.ModPersistentState;
import watizdat.rituals.state.ModPlayerData;

public class ModServerReceivers {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ModNetworkConstants.START_RITUAL_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                ((RitualPoleBlockEntity) player.getWorld().getBlockEntity(pos)).startRitual(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ModNetworkConstants.RESET_RITUAL_STATE_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                RitualPoleBlockEntity be = (RitualPoleBlockEntity) player.getWorld().getBlockEntity(pos);
                be.resetRitualState();

                ModPlayerData playerState = ModPersistentState.getPlayerState(player);

                PacketByteBuf openGuiBuf = PacketByteBufs.create();
                openGuiBuf.writeCollection(playerState.entityTypesKilled.stream().map(EntityType::getId).toList(), PacketByteBuf::writeIdentifier);
                openGuiBuf.writeBlockPos(pos);

                RitualState ritualState = be.getRitualState();
                openGuiBuf.writeEnumConstant(ritualState);

                ServerPlayNetworking.send(player, ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, openGuiBuf);
            });
        });
    }
}
