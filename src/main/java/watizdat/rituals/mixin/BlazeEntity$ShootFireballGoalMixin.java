package watizdat.rituals.mixin;

import net.minecraft.entity.mob.BlazeEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import watizdat.rituals.access.MobEntityMixinAccess;

@Mixin(BlazeEntity.ShootFireballGoal.class)
public class BlazeEntity$ShootFireballGoalMixin {
    @Shadow private int fireballCooldown;

    @Shadow @Final private BlazeEntity blaze;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/mob/BlazeEntity$ShootFireballGoal;fireballCooldown:I",
                    opcode = Opcodes.PUTFIELD),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/entity/mob/BlazeEntity$ShootFireballGoal;fireballCooldown:I",
                            opcode = Opcodes.PUTFIELD,
                            ordinal = 0,
                            shift = At.Shift.AFTER),
                    to = @At("TAIL")))
    private void rituals$decreaseFireballCooldownForRitualMobs(BlazeEntity.ShootFireballGoal instance, int value) {
        if (((MobEntityMixinAccess) blaze).rituals$isRitualMob()) {
            fireballCooldown = 6;

            return;
        }

        fireballCooldown = value;
    }
}
