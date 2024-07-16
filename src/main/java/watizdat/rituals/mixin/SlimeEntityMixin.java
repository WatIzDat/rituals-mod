package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.access.SlimeEntityMixinAccess;
import watizdat.rituals.state.ModComponents;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntityMixin implements SlimeEntityMixinAccess {
    @Unique
    private ActiveTargetGoal<PlayerEntity> playerTargetGoal;

    protected SlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void rituals$setAsRitualMob() {
        super.rituals$setAsRitualMob();

        targetSelector.remove(playerTargetGoal);
        targetSelector.add(1, new ActiveTargetGoal<>((MobEntity) (Object) this, PlayerEntity.class, false));
    }

    @Redirect(method = "initGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 4))
    private void rituals$storePlayerActiveTargetGoal(GoalSelector instance, int priority, Goal goal) {
        playerTargetGoal = new ActiveTargetGoal<>((MobEntity) (Object) this, PlayerEntity.class, 10, true, false, (livingEntity) -> {
            return Math.abs(livingEntity.getY() - this.getY()) <= 4.0;
        });

        targetSelector.add(1, playerTargetGoal);
    }

    @Inject(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private void rituals$setChildSlimesAsRitualMobs(CallbackInfo info, @Local SlimeEntity slimeEntity) {
        if (rituals$isRitualMob()) {
            ModComponents.RITUAL_POLE_POS_COMPONENT.get(slimeEntity).set(ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).getValue());

            ((MobEntityMixinAccess) slimeEntity).rituals$setAsRitualMob();

            ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).getBlockEntity(getWorld()).addEntityUuid(slimeEntity.getUuid());
        }
    }
}
