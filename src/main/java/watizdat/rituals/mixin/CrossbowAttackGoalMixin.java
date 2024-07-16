package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(CrossbowAttackGoal.class)
public abstract class CrossbowAttackGoalMixin<T extends HostileEntity & RangedAttackMob & CrossbowUser> extends Goal {
    @Shadow @Final private T actor;

    @Shadow private int chargedTicksLeft;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/CrossbowUser;setCharging(Z)V", ordinal = 1))
    private void rituals$decreaseChargedTicksForRitualMobs(CallbackInfo info) {
        if (((MobEntityMixinAccess) actor).rituals$isRitualMob()) {
            chargedTicksLeft = 5;
        }
    }
}
