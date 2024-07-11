package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.entity.goal.*;
import watizdat.rituals.init.ModStatusEffects;

import java.util.ArrayList;
import java.util.List;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements MobEntityMixinAccess {
    @Shadow protected abstract boolean isDisallowedInPeaceful();

    @Unique
    private boolean isRitualMob;

    @Shadow @Final protected GoalSelector goalSelector;

    @Shadow @Final protected GoalSelector targetSelector;

    @Shadow protected EntityNavigation navigation;

    @Shadow protected MoveControl moveControl;

    @Override
    public boolean rituals$isRitualMob() {
        return isRitualMob;
    }

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

//    @Inject(at = @At("TAIL"), method = "<init>")
//    private void rituals$registerRitualPolePosSetEventCallback(CallbackInfo info) {
//        ComponentEvents.RITUAL_POLE_POS_SET.register(this::onRitualPolePosSet);
//    }

    @Inject(at = @At("HEAD"), method = "checkDespawn", cancellable = true)
    private void rituals$preventRitualMobDespawning(CallbackInfo info) {
        if (rituals$isRitualMob()) {
            if (getWorld().getDifficulty() == Difficulty.PEACEFUL && isDisallowedInPeaceful()) {
                discard();
            } else {
                despawnCounter = 0;
            }

            info.cancel();
        }
    }

//    @Unique
//    protected ActionResult onRitualPolePosSet() {
//        isRitualMob = true;
//
//        addRitualGoals();
//
//        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
//                ModStatusEffects.RITUAL_STATUS_EFFECT, StatusEffectInstance.INFINITE, 0, false, true);
//        addStatusEffect(statusEffectInstance);
//
//        return ActionResult.PASS;
//    }

    @Override
    public void rituals$setAsRitualMob() {
        isRitualMob = true;

        addRitualGoals();

        StatusEffectInstance statusEffectInstance = new StatusEffectInstance(
                ModStatusEffects.RITUAL_STATUS_EFFECT, StatusEffectInstance.INFINITE, 0, false, true);
        addStatusEffect(statusEffectInstance);
    }

    @Unique
    private void addRitualGoals() {
        System.out.println("adding goals");

        if (((MobEntity) (Object) this) instanceof PathAwareEntity) {
            List<Goal> goalsToRemove = new ArrayList<>();

            goalSelector.getGoals().forEach(goal -> {
                if (goal.getGoal() instanceof FleeEntityGoal<?> ||
                    goal.getGoal() instanceof EscapeDangerGoal) {

                    goalsToRemove.add(goal.getGoal());
                }
            });

            for (Goal goal : goalsToRemove) {
                goalSelector.remove(goal);
            }

            goalSelector.add(3, new MoveToRitualPoleGoal((PathAwareEntity) (Object) this, 1.1, 50));
            goalSelector.add(2, new LookAtEntityGoal((MobEntity) (Object) this, PlayerEntity.class, 8f));
            goalSelector.add(2, new LookAroundGoal((MobEntity) (Object) this));
            goalSelector.add(1, new MeleeAttackGoal((PathAwareEntity) (Object) this, 1d, false));
            targetSelector.add(1, new ActiveTargetGoal<>((MobEntity) (Object) this, PlayerEntity.class, false));
        }
    }

    @Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
    private void rituals$writeIsRitualMobToNbt(CallbackInfo info, @Local(argsOnly = true) NbtCompound nbt) {
        nbt.putBoolean("IsRitualMob", rituals$isRitualMob());
    }

    @Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
    private void rituals$readIsRitualMobFromNbt(CallbackInfo info, @Local(argsOnly = true) NbtCompound nbt) {
        isRitualMob = nbt.getBoolean("IsRitualMob");

        if (isRitualMob) {
            rituals$setAsRitualMob();
        }
    }
}
