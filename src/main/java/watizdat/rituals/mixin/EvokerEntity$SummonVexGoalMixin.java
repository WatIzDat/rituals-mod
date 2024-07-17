package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.VexEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.entity.ModEntityHelper;
import watizdat.rituals.state.ModComponents;

@Mixin(EvokerEntity.SummonVexGoal.class)
public class EvokerEntity$SummonVexGoalMixin {
    @Shadow @Final EvokerEntity field_7267;

    @Inject(method = "castSpell", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntityAndPassengers(Lnet/minecraft/entity/Entity;)V"))
    private void rituals$setSummonedVexesAsRitualMobs(CallbackInfo info, @Local VexEntity vexEntity) {
        if (((MobEntityMixinAccess) field_7267).rituals$isRitualMob()) {
            ModEntityHelper.setAsRitualMob(vexEntity, ModComponents.RITUAL_POLE_POS_COMPONENT.get(field_7267).getValue());
        }
    }
}
