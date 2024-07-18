package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.*;
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

    @Override
    public void rituals$setAsRitualMob() {
        isRitualMob = true;

        addRitualGoals();
    }

    @Unique
    private void addRitualGoals() {
        System.out.println("adding goals");

        if (((MobEntity) (Object) this) instanceof PathAwareEntity) {
            List<Goal> goalsToRemove = new ArrayList<>();

            // TODO: Could convert these to instanceof checks
            boolean hasCrossbowAttackGoal = false;
            boolean hasBlazeEntityShootFireballGoal = false;

            boolean isVexEntity = ((MobEntity) (Object) this) instanceof VexEntity;
            boolean isGuardianEntity = ((MobEntity) (Object) this) instanceof GuardianEntity;

            for (PrioritizedGoal goal : goalSelector.getGoals()) {
                if (goal.getGoal() instanceof FleeEntityGoal<?> ||
                    goal.getGoal() instanceof EscapeDangerGoal ||
                    goal.getGoal() instanceof GuardianEntity.FireBeamGoal) {

                    goalsToRemove.add(goal.getGoal());
                }

                if (goal.getGoal() instanceof CrossbowAttackGoal<?>) {
                    hasCrossbowAttackGoal = true;
                }

                if (goal.getGoal() instanceof BlazeEntity.ShootFireballGoal) {
                    hasBlazeEntityShootFireballGoal = true;
                }
            }

            for (Goal goal : goalsToRemove) {
                goalSelector.remove(goal);
            }

            if (!isVexEntity) {
                goalSelector.add(isGuardianEntity ? 10 : 3, new MoveToRitualPoleGoal((PathAwareEntity) (Object) this, 1.1, 50));
            }
            if (!isGuardianEntity) {
                goalSelector.add(2, new LookAtEntityGoal((MobEntity) (Object) this, PlayerEntity.class, 8f));
                goalSelector.add(2, new LookAroundGoal((MobEntity) (Object) this));
            }
            if (!hasCrossbowAttackGoal && !hasBlazeEntityShootFireballGoal && !isVexEntity && !isGuardianEntity) {
                goalSelector.add(1, new MeleeAttackGoal((PathAwareEntity) (Object) this, 1d, false));
            } else if (hasCrossbowAttackGoal) {
                goalSelector.add(2, new CrossbowAttackGoal<>((HostileEntity & CrossbowUser) (Object) this, 1.0, 48.0F));
            } else if (hasBlazeEntityShootFireballGoal) {
                goalSelector.add(1, new MeleeAttackGoal((PathAwareEntity) (Object) this, 1d, false));
                goalSelector.add(2, new BlazeEntity.ShootFireballGoal((BlazeEntity) (Object) this));
            } else if (isGuardianEntity) {
                goalSelector.add(1, new GuardianEntity.FireBeamGoal((GuardianEntity) (Object) this));
            }
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
