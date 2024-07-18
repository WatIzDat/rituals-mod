package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuardianEntity.FireBeamGoal.class)
public class GuardianEntity$FireBeamGoalMixin {
    @Shadow @Final private GuardianEntity guardian;

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/GuardianEntity;canSee(Lnet/minecraft/entity/Entity;)Z"))
    private boolean rituals$allowRitualGuardianToSeeThroughOneBlock(boolean original, @Local LivingEntity target) {
        if (original) {
            return true;
        }

        boolean twoBlocksInTheWay = false;

        Vec3d start = new Vec3d(guardian.getX(), guardian.getEyeY(), guardian.getZ());
        Vec3d end = new Vec3d(target.getX(), target.getEyeY(), target.getZ());

        BlockHitResult hitResult1 = guardian.getWorld().raycast(new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                guardian));

        if (hitResult1.getType() == HitResult.Type.BLOCK) {
            Vec3d startWithOffset = hitResult1.getPos();

            Direction.Axis axis = hitResult1.getSide().getAxis();

            if (axis == Direction.Axis.X) {
                startWithOffset.add(0d, 0d, 1d);
            } else if (axis == Direction.Axis.Z) {
                startWithOffset.add(1d, 0d, 0d);
            }

            HitResult hitResult2 = guardian.getWorld().raycast(new RaycastContext(
                    startWithOffset,
                    end,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    guardian));
        }

        return !original && !twoBlocksInTheWay;
    }
}
