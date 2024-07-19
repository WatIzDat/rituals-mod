package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuardianEntity.class)
public abstract class GuardianEntityMixin extends MobEntityMixin {
    @Unique
    private ActiveTargetGoal<LivingEntity> activeTargetGoal;

    protected GuardianEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 6))
    private void rituals$storeActiveTargetGoal(GoalSelector instance, int priority, Goal goal, Operation<Void> original) {
        activeTargetGoal = (ActiveTargetGoal<LivingEntity>) goal;

        original.call(instance, priority, goal);
    }

    @ModifyReturnValue(method = "getWarmupTime", at = @At("RETURN"))
    private int rituals$decreaseWarmupTimeForRitualMobs(int original) {
        if (rituals$isRitualMob()) {
            return original / 4;
        }

        return original;
    }

    @Override
    public void rituals$setAsRitualMob() {
        targetSelector.remove(activeTargetGoal);

        super.rituals$setAsRitualMob();
    }
}
