package watizdat.rituals.state;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.Rituals;

public class ModDataAttachments {
    public static final AttachmentType<BlockPos> RITUAL_POLE_POS_PERSISTENT = AttachmentRegistry.createPersistent(
            Rituals.id("ritual_pole_pos_persistent"),
            BlockPos.CODEC
    );
}
