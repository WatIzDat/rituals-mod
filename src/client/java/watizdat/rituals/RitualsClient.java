package watizdat.rituals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import watizdat.rituals.network.ModNetworkConstants;

import java.util.ArrayList;
import java.util.List;

public class RitualsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, ((client, handler, buf, responseSender) -> {
			client.execute(() -> {
				client.setScreen(new FatalErrorScreen(Text.literal("test"), Text.literal("test")));
			});
		}));

		ClientPlayNetworking.registerGlobalReceiver(ModNetworkConstants.KILLED_ENTITY_PACKET_ID, ((client, handler, buf, responseSender) -> {
			List<Identifier> entityTypesKilled = buf.readCollection(ArrayList::new, PacketByteBuf::readIdentifier);

			client.execute(() -> {
				entityTypesKilled.forEach(id -> {
					client.player.sendMessage(Text.literal(id.toString()));
				});
			});
		}));
	}
}