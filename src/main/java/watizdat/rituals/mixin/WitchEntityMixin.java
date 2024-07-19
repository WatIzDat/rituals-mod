package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.ProjectileAttackGoalMixinAccess;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends MobEntityMixin {
    @Unique
    private ProjectileAttackGoal projectileAttackGoal;

    protected WitchEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 1))
    private void rituals$storeProjectileAttackGoal(GoalSelector instance, int priority, Goal goal, Operation<Void> original) {
        projectileAttackGoal = (ProjectileAttackGoal) goal;

        original.call(instance, priority, goal);
    }

    @WrapOperation(method = "attack", at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;SPLASH_POTION:Lnet/minecraft/item/Item;"))
    private Item rituals$throwLingeringPotionWhenFarAwayForRitualMobs(Operation<Item> original, @Local(ordinal = 3) double distanceFromTarget) {
        if (rituals$isRitualMob()) {
            if (distanceFromTarget >= 5) {
                ((ProjectileAttackGoalMixinAccess) projectileAttackGoal).rituals$setIntervalTicks(30);

                return Items.LINGERING_POTION;
            } else {
                ((ProjectileAttackGoalMixinAccess) projectileAttackGoal).rituals$setIntervalTicks(5);
            }
        }

        return original.call();
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxUseTime()I"))
    private int rituals$decreaseDrinkTimeForRitualMobs(int original) {
        if (rituals$isRitualMob()) {
            return original / 4;
        }

        return original;
    }
}
