package watizdat.rituals.entity.goal;

import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.mob.MobEntity;
import watizdat.rituals.access.MobEntityMixinAccess;

public class RitualLookAroundGoal extends LookAroundGoal {
    private final MobEntity mobAccess;

    public RitualLookAroundGoal(MobEntity mob) {
        super(mob);

        mobAccess = mob;
    }

    @Override
    public boolean canStart() {
        if (!((MobEntityMixinAccess) mobAccess).rituals$isRitualMob()) {
            return false;
        }

        return super.canStart();
    }
}
