package watizdat.rituals.network;

import net.minecraft.util.Identifier;
import watizdat.rituals.Rituals;

public class ModNetworkConstants {
    public static final Identifier OPEN_RITUAL_POLE_GUI_PACKET_ID = Rituals.id("open_ritual_pole_gui");
    public static final Identifier KILLED_ENTITY_PACKET_ID = Rituals.id("killed_entity");
    public static final Identifier START_RITUAL_PACKET_ID = Rituals.id("start_ritual");
    public static final Identifier RESET_RITUAL_STATE_PACKET_ID = Rituals.id("reset_ritual_state");
}
