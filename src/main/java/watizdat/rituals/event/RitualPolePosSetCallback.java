//package watizdat.rituals.event;
//
//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
//import net.minecraft.util.ActionResult;
//
//public interface RitualPolePosSetCallback {
//    Event<RitualPolePosSetCallback> EVENT = EventFactory.createArrayBacked(RitualPolePosSetCallback.class,
//            (listeners) -> () -> {
//                for (RitualPolePosSetCallback listener : listeners) {
//                    ActionResult result = listener.setAsRitualMob();
//
//                    if (result != ActionResult.PASS) {
//                        return result;
//                    }
//                }
//
//                return ActionResult.PASS;
//            });
//
//    ActionResult setAsRitualMob();
//}
