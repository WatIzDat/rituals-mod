package watizdat.rituals.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import watizdat.rituals.access.BatEntityMixinAccess;

@Mixin(BatEntity.class)
public abstract class BatEntityMixin extends MobEntityMixin implements BatEntityMixinAccess {
    @Shadow public abstract void setRoosting(boolean roosting);

    protected BatEntityMixin(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void rituals$setAsRitualMob() {
        super.rituals$setAsRitualMob();

        moveControl = new FlightMoveControl((PathAwareEntity) (Object) this, 20, true);

        BirdNavigation birdNavigation = new BirdNavigation((PathAwareEntity) (Object) this, getWorld());
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        navigation = birdNavigation;

        setRoosting(false);
    }

    @Inject(at = @At("HEAD"), method = "mobTick", cancellable = true)
    private void rituals$cancelMobTickForRitualMobs(CallbackInfo info) {
        if (rituals$isRitualMob()) {
            info.cancel();
        }
    }

    @Inject(method = "getMoveEffect", at = @At("HEAD"), cancellable = true)
    private void rituals$setGetMoveEffectToAll(CallbackInfoReturnable<Entity.MoveEffect> info) {
        info.setReturnValue(MoveEffect.ALL);
    }
}
