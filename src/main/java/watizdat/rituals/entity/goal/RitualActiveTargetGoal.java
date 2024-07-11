package watizdat.rituals.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import watizdat.rituals.access.MobEntityMixinAccess;

import java.util.function.Predicate;

public class RitualActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
    public RitualActiveTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        super(mob, targetClass, checkVisibility);
    }

    public RitualActiveTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    public boolean canStart() {
        if (!((MobEntityMixinAccess) mob).rituals$isRitualMob()) {
            return false;
        }

        return super.canStart();
    }
}
