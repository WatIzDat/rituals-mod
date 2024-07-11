package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.entity.goal.*;
import watizdat.rituals.event.ComponentEvents;
import watizdat.rituals.state.ModComponents;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements MobEntityMixinAccess {
    @Shadow protected abstract boolean isDisallowedInPeaceful();

//    @Unique
//    private boolean preventDespawning;

    @Unique
    private boolean isRitualMob;

    @Shadow @Final protected GoalSelector goalSelector;

    @Shadow @Final protected GoalSelector targetSelector;

    @Override
    public boolean rituals$isRitualMob() {
//        return ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).isPresent();
        return isRitualMob;
    }

//    @Override
//    public void rituals$setAsRitualMob() {
//        isRitualMob = true;
//    }

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

//    @Override
//    public void rituals$addRitualModifiers() {
//        if (getAttributes().hasAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED)) {
//            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier(
//                    "Entity movement speed",
//                    1.5d,
//                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
//            ));
//        }
//
//        if (getAttributes().hasAttribute(EntityAttributes.GENERIC_FLYING_SPEED)) {
//            getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED).addPersistentModifier(new EntityAttributeModifier(
//                    "Entity flying speed",
//                    1.5d,
//                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
//            ));
//        }
//
//        if (getAttributes().hasAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE)) {
//            getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier(
//                    "Entity follow range",
//                    100d,
//                    EntityAttributeModifier.Operation.ADDITION
//            ));
//        }
//    }
//
//    @Override
//    public void rituals$preventDespawning() {
//        preventDespawning = true;
//    }
//
    @Inject(at = @At("TAIL"), method = "<init>")
    private void rituals$registerRitualPolePosSetEventCallback(CallbackInfo info) {
        ComponentEvents.RITUAL_POLE_POS_SET.register(() -> {
            isRitualMob = true;

            addRitualGoals();

            return ActionResult.PASS;
        });
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

//    @Inject(at = @At("TAIL"), method = "<init>")
//    private void rituals$addRitualGoals(CallbackInfo info) {
//        System.out.println("adding goals");
//
//        if (((MobEntity) (Object) this) instanceof PathAwareEntity) {
//            goalSelector.add(3, new MoveToRitualPoleGoal((PathAwareEntity) (Object) this, 1.1, 50));
//            goalSelector.add(2, new RitualLookAtEntityGoal((MobEntity) (Object) this, PlayerEntity.class, 8f));
//            goalSelector.add(2, new RitualLookAroundGoal((MobEntity) (Object) this));
//            goalSelector.add(1, new RitualMeleeAttackGoal((PathAwareEntity) (Object) this, 1d, false));
//            targetSelector.add(1, new RitualActiveTargetGoal<>((MobEntity) (Object) this, PlayerEntity.class, false));
//        }
//    }

    @Unique
    private void addRitualGoals() {
        System.out.println("adding goals");

        if (((MobEntity) (Object) this) instanceof PathAwareEntity) {
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
            addRitualGoals();
        }
    }

//    @Inject(at = @At("HEAD"), method = "initGoals")
//    private void rituals$addRitualGoals(CallbackInfo info) {
//        System.out.println("adding goals");
//
//        if (((MobEntity) (Object) this) instanceof PathAwareEntity) {
//
//
//            goalSelector.add(3, new MoveToRitualPoleGoal((PathAwareEntity) (Object) this, 1.1, 50));
//            goalSelector.add(2, new RitualLookAtEntityGoal((MobEntity) (Object) this, PlayerEntity.class, 8f));
//            goalSelector.add(2, new RitualLookAroundGoal((MobEntity) (Object) this));
//            goalSelector.add(1, new RitualMeleeAttackGoal((PathAwareEntity) (Object) this, 1d, false));
//            targetSelector.add(1, new RitualActiveTargetGoal<>((MobEntity) (Object) this, PlayerEntity.class, false));
//        }
//    }
//
//    @Inject(at = @At("RETURN"), method = "createMobAttributes", cancellable = true)
//    private static void rituals$createMobAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
//        info.setReturnValue(info.getReturnValue()
//                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0d)
//                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0d)
//                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0d));
//    }
}
