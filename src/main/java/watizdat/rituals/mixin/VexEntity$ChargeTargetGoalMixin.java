package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.mob.VexEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(targets = "net.minecraft.entity.mob.VexEntity$ChargeTargetGoal")
public class VexEntity$ChargeTargetGoalMixin {
    @Shadow @Final VexEntity field_7412;

    @ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "doubleValue=1.0"))
    private double rituals$increaseChargeSpeedForRitualMobs(double original) {
        if (((MobEntityMixinAccess) field_7412).rituals$isRitualMob()) {
            return original * 3;
        }

        return original;
    }
}
