package watizdat.rituals.mixin;

import net.minecraft.entity.mob.SlimeEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import watizdat.rituals.access.SlimeEntityMixinAccess;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$FaceTowardTargetGoal")
public abstract class SlimeEntity$FaceTowardTargetGoalMixin {
    @Shadow @Final private SlimeEntity slime;

    @ModifyConstant(method = "start", constant = @Constant(intValue = 300))
    private int rituals$startTicks(int constant) {
        if (((SlimeEntityMixinAccess) slime).rituals$isAggressive()) {
            return Integer.MAX_VALUE;
        }

        return constant;
    }

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 10f))
    private float rituals$tickPitchChange(float constant) {
        if (((SlimeEntityMixinAccess) slime).rituals$isAggressive()) {
            return Float.MAX_VALUE;
        }

        return constant;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/SlimeEntity$SlimeMoveControl;look(FZ)V"), index = 1)
    private boolean rituals$tickCanAttack(boolean canAttack) {
        if (((SlimeEntityMixinAccess) slime).rituals$isAggressive()) {
            return true;
        }

        return canAttack;
    }
}
