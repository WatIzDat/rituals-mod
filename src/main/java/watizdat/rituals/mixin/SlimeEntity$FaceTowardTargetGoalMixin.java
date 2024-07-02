package watizdat.rituals.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$FaceTowardTargetGoal")
public class SlimeEntity$FaceTowardTargetGoalMixin {
    @ModifyConstant(method = "start", constant = @Constant(intValue = 300))
    private int rituals$startTicks(int constant) {
        return Integer.MAX_VALUE;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 10f))
    private float rituals$tickPitchChange(float constant) {
        return Float.MAX_VALUE;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SlimeEntity$SlimeMoveControl;look(FZ)V"), index = 1)
    private boolean rituals$tickCanAttack(boolean canAttack) {
        return true;
    }
}
