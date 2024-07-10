package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements MobEntityMixinAccess {
    @Shadow protected abstract boolean isDisallowedInPeaceful();

    @Unique
    private boolean preventDespawning;

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void rituals$addRitualModifiers() {
        if (getAttributes().hasAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED)) {
            getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier(
                    "Entity movement speed",
                    1.5d,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        if (getAttributes().hasAttribute(EntityAttributes.GENERIC_FLYING_SPEED)) {
            getAttributeInstance(EntityAttributes.GENERIC_FLYING_SPEED).addPersistentModifier(new EntityAttributeModifier(
                    "Entity flying speed",
                    1.5d,
                    EntityAttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }

        if (getAttributes().hasAttribute(EntityAttributes.GENERIC_FOLLOW_RANGE)) {
            getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier(
                    "Entity follow range",
                    100d,
                    EntityAttributeModifier.Operation.ADDITION
            ));
        }
    }

    @Override
    public void rituals$preventDespawning() {
        preventDespawning = true;
    }

    @Inject(at = @At("HEAD"), method = "checkDespawn", cancellable = true)
    private void rituals$checkDespawn(CallbackInfo info) {
        if (preventDespawning) {
            if (getWorld().getDifficulty() == Difficulty.PEACEFUL && isDisallowedInPeaceful()) {
                discard();
            } else {
                despawnCounter = 0;
            }

            info.cancel();
        }
    }

    @Inject(at = @At("RETURN"), method = "createMobAttributes", cancellable = true)
    private static void rituals$createMobAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.setReturnValue(info.getReturnValue()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0d)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0d)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0d));
    }
}
