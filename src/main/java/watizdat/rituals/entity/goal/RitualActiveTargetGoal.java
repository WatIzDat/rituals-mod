package watizdat.rituals.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import watizdat.rituals.access.MobEntityMixinAccess;

public class RitualActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
    public RitualActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        super(mob, targetClass, checkVisibility);
    }

    @Override
    public boolean canStart() {
        if (!((MobEntityMixinAccess) mob).rituals$isRitualMob()) {
            return false;
        }

        return super.canStart();
    }
}
