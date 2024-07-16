package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.EndermanEntity$TeleportTowardsPlayerGoalMixinAccess;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$TeleportTowardsPlayerGoal")
public class EndermanEntity$TeleportTowardsPlayerGoalMixin implements EndermanEntity$TeleportTowardsPlayerGoalMixinAccess {
    @Shadow @Final private TargetPredicate staringPlayerPredicate;

    @Shadow @Final private EndermanEntity enderman;

    @ModifyExpressionValue(method = "shouldContinue", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private boolean rituals$setRitualMobsAsAlwaysAngry(boolean original) {
        if (((MobEntityMixinAccess) enderman).rituals$isRitualMob()) {
            return true;
        }

        return original;
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;squaredDistanceTo(Lnet/minecraft/entity/Entity;)D", ordinal = 1))
    private double rituals$allowTeleportForRitualMobs(double original) {
        if (((MobEntityMixinAccess) enderman).rituals$isRitualMob()) {
            return Integer.MAX_VALUE;
        }

        return original;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/EndermanEntity;teleportTo(Lnet/minecraft/entity/Entity;)Z"))
    private boolean rituals$teleportRitualMobsExactly(EndermanEntity instance, Entity entity, Operation<Boolean> original) {
        if (((MobEntityMixinAccess) enderman).rituals$isRitualMob()) {
            return enderman.teleportTo(entity.getX(), entity.getY(), entity.getZ());
        }

        return original.call(instance, entity);
    }

    @Override
    public void rituals$setAsRitualGoal() {
        staringPlayerPredicate.setPredicate(null);
    }
}
