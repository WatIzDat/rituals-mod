package watizdat.rituals.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public class ComponentEvents {
    public static final Event<RitualPolePosSet> RITUAL_POLE_POS_SET = EventFactory.createArrayBacked(RitualPolePosSet.class,
            (callbacks) -> () -> {
                for (RitualPolePosSet callback : callbacks) {
                    ActionResult result = callback.onSet();

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    @FunctionalInterface
    public interface RitualPolePosSet {
        ActionResult onSet();
    }
}
