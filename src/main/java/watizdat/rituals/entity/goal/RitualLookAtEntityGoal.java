package watizdat.rituals.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;
import watizdat.rituals.access.MobEntityMixinAccess;

public class RitualLookAtEntityGoal extends LookAtEntityGoal {
    public RitualLookAtEntityGoal(MobEntity mob, Class<? extends LivingEntity> targetType, float range) {
        super(mob, targetType, range);
    }

    @Override
    public boolean canStart() {
        if (!((MobEntityMixinAccess) mob).rituals$isRitualMob()) {
            return false;
        }

        return super.canStart();
    }
}
