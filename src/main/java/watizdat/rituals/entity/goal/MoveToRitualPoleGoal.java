package watizdat.rituals.entity.goal;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.state.ModComponents;

import java.util.EnumSet;

public class MoveToRitualPoleGoal extends MoveToTargetPosGoal {
    public MoveToRitualPoleGoal(PathAwareEntity mob, double speed, int range) {
        super(mob, speed, range, 50);
        setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!((MobEntityMixinAccess) mob).rituals$isRitualMob()) {
            return false;
        }

        return super.canStart();
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        boolean isTarget = ModComponents.RITUAL_POLE_POS_COMPONENT.get(mob).getValue().equals(pos);

        if (isTarget) {
            System.out.println(pos.toShortString());
            System.out.println("is target");
        }

        return isTarget;
    }

    @Override
    protected int getInterval(PathAwareEntity mob) {
        return 10;
    }
}
