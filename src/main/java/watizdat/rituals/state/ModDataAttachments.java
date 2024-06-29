package watizdat.rituals.state;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.Rituals;

public class ModDataAttachments {
    private static AttachmentType<BlockPos> ritualPolePosPersistent;

    public static void init() {
        ritualPolePosPersistent = AttachmentRegistry.<BlockPos>builder()
                .persistent(BlockPos.CODEC)
                .copyOnDeath()
                .buildAndRegister(Rituals.id("ritual_pole_pos_persistent"));
    }

    public static AttachmentType<BlockPos> getRitualPolePosPersistent() {
        return ritualPolePosPersistent;
    }
}
