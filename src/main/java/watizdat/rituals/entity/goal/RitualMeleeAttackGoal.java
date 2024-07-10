package watizdat.rituals.entity.goal;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import watizdat.rituals.access.MobEntityMixinAccess;

public class RitualMeleeAttackGoal extends MeleeAttackGoal {
    public RitualMeleeAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }

    @Override
    public boolean canStart() {
        if (!((MobEntityMixinAccess) mob).rituals$isRitualMob()) {
            return false;
        }

        return super.canStart();
    }
}
