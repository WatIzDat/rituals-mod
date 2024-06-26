package watizdat.rituals.entity.goal;

import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import watizdat.rituals.state.ModDataAttachments;

import java.util.EnumSet;

public class MoveToRitualPoleGoal extends MoveToTargetPosGoal {
    public MoveToRitualPoleGoal(PathAwareEntity mob, double speed, int range) {
        super(mob, speed, range, 10);
        setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        boolean isTarget = mob.getAttached(ModDataAttachments.getRitualPolePosPersistent()).equals(pos);

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
