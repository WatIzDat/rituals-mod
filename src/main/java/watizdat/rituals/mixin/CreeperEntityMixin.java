package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.state.ModComponents;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends MobEntityMixin {
    protected CreeperEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "explode", at = @At(value = "STORE"))
    private float rituals$modifyExplosionPowerMultiplier(float f) {
        if (rituals$isRitualMob()) {
            return 1.5f;
        }

        return f;
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/CreeperEntity;discard()V"))
    private void rituals$removeEntityUuidOnExplosion(CallbackInfo info) {
        if (rituals$isRitualMob()) {
            ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).getBlockEntity(getWorld()).removeEntityUuid(getUuid());
        }
    }

    @Inject(at = @At("HEAD"), method = "spawnEffectsCloud", cancellable = true)
    private void rituals$preventSpawningEffectsCloud(CallbackInfo info) {
        if (rituals$isRitualMob()) {
            info.cancel();
        }
    }
}
