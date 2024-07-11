package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import watizdat.rituals.access.SlimeEntityMixinAccess;
import watizdat.rituals.entity.goal.RitualSlimeActiveTargetGoal;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntity implements SlimeEntityMixinAccess {
    @Unique
    private boolean isAggressive;

    @Unique
    private ActiveTargetGoal<PlayerEntity> playerTargetGoal;

    protected SlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

//    @Override
//    public boolean rituals$isAggressive() {
//        return isAggressive;
//    }
//
//    @Override
//    public void rituals$setAsAggressive() {
//        isAggressive = true;
//
//        targetSelector.remove(playerTargetGoal);
//        targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
//    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 4))
    private void rituals$storePlayerActiveTargetGoal(GoalSelector instance, int priority, Goal goal) {
        playerTargetGoal = new RitualSlimeActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (livingEntity) -> {
            return Math.abs(livingEntity.getY() - this.getY()) <= 4.0;
        });

        targetSelector.add(1, playerTargetGoal);
    }
}
