package watizdat.rituals;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import watizdat.rituals.init.*;

public class Rituals implements ModInitializer {
    public static final String MOD_ID = "rituals";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ModItems.init();
        ModBlocks.init();
        ModBlockEntityTypes.init();
        ModFeatures.init();
        ModEvents.init();
        ModServerReceivers.init();
    }
}