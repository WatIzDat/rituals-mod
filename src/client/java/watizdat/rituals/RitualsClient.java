package watizdat.rituals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.text.Text;
import watizdat.rituals.network.ModNetworkConstants;

public class RitualsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ModNetworkConstants.OPEN_RITUAL_POLE_GUI_PACKET_ID, ((client, handler, buf, responseSender) -> {
			client.execute(() -> {
				client.setScreen(new FatalErrorScreen(Text.literal("test"), Text.literal("test")));
			});
		}));
	}
}