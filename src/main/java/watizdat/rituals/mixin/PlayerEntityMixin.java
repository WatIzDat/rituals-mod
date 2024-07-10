package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.init.ModDamageTypes;
import watizdat.rituals.state.ModComponents;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "tickMovement")
    private void rituals$tickMovement(CallbackInfo info) {
        if (ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).isPresent()) {
//            BlockPos ritualPolePos = getAttached(ModDataAttachments.getRitualPolePosPersistent());

            if (ModComponents.RITUAL_POLE_POS_COMPONENT.get(this).getBlockEntity(getWorld()).isOutsideCircle(getPos().getX(), getPos().getZ())) {
                damage(ModDamageTypes.of(getWorld(), ModDamageTypes.OUTSIDE_RITUAL_CIRCLE), 10f);
            }
        }
    }
}
