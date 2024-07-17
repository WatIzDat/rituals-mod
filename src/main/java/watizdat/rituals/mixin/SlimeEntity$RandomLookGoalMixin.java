package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.state.ModComponents;

@Mixin(targets = "net.minecraft.entity.mob.SlimeEntity$RandomLookGoal")
public class SlimeEntity$RandomLookGoalMixin {
    @Shadow @Final private SlimeEntity slime;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I", ordinal = 1))
    private int rituals$lookTowardsRitualPoleIfRitualMob(Random instance, int bound, Operation<Integer> original) {
        if (((MobEntityMixinAccess) slime).rituals$isRitualMob()) {
            BlockPos target = ModComponents.RITUAL_POLE_POS_COMPONENT.get(slime).getValue();
            Vec3d vec3d = EntityAnchorArgumentType.EntityAnchor.EYES.positionAt(slime);

            double d = target.getX() - vec3d.getX();
            double f = target.getZ() - vec3d.getZ();

            return MathHelper.floor(MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 180.0F / (float) Math.PI) - 90.0F));
        }

        return original.call(instance, bound);
    }
}
