package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import watizdat.rituals.access.BatEntityMixinAccess;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends AmbientEntity implements BatEntityMixinAccess {
    @Shadow public abstract void setRoosting(boolean roosting);

    @Unique
    private boolean isAggressive;

    protected BatEntityMixin(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation((PathAwareEntity) (Object) this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

//    @Override
//    public void rituals$setAsAggressive() {
//        isAggressive = true;
//
////        getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED).addPersistentModifier(new EntityAttributeModifier(
////                "Bat flying speed",
////                0.6,
////                EntityAttributeModifier.Operation.ADDITION
////        ));
////
////        getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier(
////                "Bat movement speed",
////                0.3,
////                EntityAttributeModifier.Operation.ADDITION
////        ));
//    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void rituals$init(CallbackInfo info, @Local(argsOnly = true) World world) {
//        if (!world.isClient) {
//            setRoosting(false);
//        }

        moveControl = new FlightMoveControl((PathAwareEntity) (Object) this, 20, true);
    }

    @Inject(at = @At("HEAD"), method = "mobTick", cancellable = true)
    private void rituals$mobTick(CallbackInfo info) {
//        if (isAggressive) {
//            info.cancel();
//        }

        if (((MobEntityMixinAccess) this).rituals$isRitualMob()) {
            setRoosting(false);

            info.cancel();
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/AmbientEntity;tick()V", shift = At.Shift.AFTER), cancellable = true)
    private void rituals$test(CallbackInfo info) {
        info.cancel();
    }

    @Inject(method = "getMoveEffect", at = @At("HEAD"), cancellable = true)
    private void rituals$getMoveEffect(CallbackInfoReturnable<Entity.MoveEffect> info) {
        info.setReturnValue(MoveEffect.ALL);
    }
}
