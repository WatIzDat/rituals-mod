package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.mob.GuardianEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(GuardianEntity.FireBeamGoal.class)
public class GuardianEntity$FireBeamGoalMixin {
    @Shadow @Final private GuardianEntity guardian;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/GuardianEntity;getWarmupTime()I"))
    private int rituals$decreaseWarmupTimeForRitualMobsFireBeamGoalCallSite(int original) {
        if (((MobEntityMixinAccess) guardian).rituals$isRitualMob()) {
            return original / 4;
        }

        return original;
    }
}
