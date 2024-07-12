package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @ModifyReturnValue(at = @At("RETURN"), method = "getPullTime")
    private static int rituals$decreasePullTimeForRitualMobs(int original, @Local(argsOnly = true) ItemStack stack) {
        if (!stack.isEmpty() &&
                stack.getHolder() instanceof MobEntity &&
                ((MobEntityMixinAccess) stack.getHolder()).rituals$isRitualMob()) {

            return original / 4;
        }

        return original;
    }

    @ModifyVariable(method = "shoot", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private static ProjectileEntity rituals$increaseDamageOfProjectileForRitualMobs(ProjectileEntity original, @Local(argsOnly = true) LivingEntity shooter) {
        if (((MobEntityMixinAccess) shooter).rituals$isRitualMob()) {
            ((PersistentProjectileEntity) original).setDamage(((PersistentProjectileEntity) original).getDamage() + 10d);
        }

        return original;
    }
}
