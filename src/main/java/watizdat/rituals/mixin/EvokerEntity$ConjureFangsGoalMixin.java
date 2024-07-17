package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.mob.EvokerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(targets = "net.minecraft.entity.mob.EvokerEntity$ConjureFangsGoal")
public class EvokerEntity$ConjureFangsGoalMixin {
    @Shadow @Final EvokerEntity field_7265;

    @ModifyReturnValue(method = "startTimeDelay", at = @At("RETURN"))
    private int rituals$decreaseStartTimeDelayForRitualMobs(int original) {
        if (((MobEntityMixinAccess) field_7265).rituals$isRitualMob()) {
            return original / 3;
        }

        return original;
    }
}
