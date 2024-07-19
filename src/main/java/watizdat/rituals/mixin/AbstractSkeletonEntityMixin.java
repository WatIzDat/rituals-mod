package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends MobEntityMixin {
    protected AbstractSkeletonEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private PersistentProjectileEntity rituals$increaseDamageOfProjectileForRitualMobs(PersistentProjectileEntity original) {
        if (rituals$isRitualMob()) {
            original.setDamage(original.getDamage() + 10d);
        }

        return original;
    }
}
