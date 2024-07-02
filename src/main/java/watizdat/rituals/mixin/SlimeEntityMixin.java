package watizdat.rituals.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$FaceTowardTargetGoal")
public class SlimeEntityMixin {
    @ModifyConstant(method = "start", constant = @Constant(intValue = 300))
    private int rituals$startTicks(int constant) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 10f))
    private float rituals$tickPitchChange(float constant) {
        return 360f;
    }
}
