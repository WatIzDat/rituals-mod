package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.EndermanEntity$TeleportTowardsPlayerGoalMixinAccess;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends MobEntityMixin {
    @Unique
    private EndermanEntity.TeleportTowardsPlayerGoal teleportTowardsPlayerGoal;

    protected EndermanEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 8))
    private void rituals$storeTeleportTowardsPlayerGoal(GoalSelector instance, int priority, Goal goal, Operation<Void> original) {
        teleportTowardsPlayerGoal = (EndermanEntity.TeleportTowardsPlayerGoal) goal;

        original.call(instance, priority, goal);
    }

    @Override
    public void rituals$setAsRitualMob() {
        super.rituals$setAsRitualMob();

        ((EndermanEntity$TeleportTowardsPlayerGoalMixinAccess) teleportTowardsPlayerGoal).rituals$setAsRitualGoal();
    }
}
