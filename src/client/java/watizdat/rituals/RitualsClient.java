package watizdat.rituals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.enums.RitualPoleType;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.network.ModNetworkConstants;
import watizdat.rituals.screen.RitualPoleUIModelScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RitualsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, ((client, handler, buf, responseSender) -> {
			List<Map.Entry<Identifier, Integer>> entityTypesKilled =
					buf.readCollection(ArrayList::new, packetByteBuf ->
							Map.entry(packetByteBuf.readIdentifier(), packetByteBuf.readInt()));

			BlockPos pos = buf.readBlockPos();
			RitualState ritualState = buf.readEnumConstant(RitualState.class);
			RitualPoleType ritualPoleType = buf.readEnumConstant(RitualPoleType.class);

			client.execute(() -> {
				client.setScreen(new RitualPoleUIModelScreen(ritualState, entityTypesKilled, pos, ritualPoleType));
			});
		}));

		ClientPlayNetworking.registerGlobalReceiver(ModNetworkConstants.KILLED_ENTITY_PACKET_ID, ((client, handler, buf, responseSender) -> {
			List<Map.Entry<Identifier, Integer>> entityTypesKilled =
					buf.readCollection(ArrayList::new, packetByteBuf ->
							Map.entry(packetByteBuf.readIdentifier(), packetByteBuf.readInt()));

			client.execute(() -> {
				entityTypesKilled.forEach(entry -> {
					client.player.sendMessage(Text.literal(entry.getKey().toString() + " " + entry.getValue()));
				});
			});
		}));
	}
}