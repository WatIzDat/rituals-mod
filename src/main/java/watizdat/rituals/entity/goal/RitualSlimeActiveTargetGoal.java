package watizdat.rituals.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;
import watizdat.rituals.access.MobEntityMixinAccess;

import java.util.function.Predicate;

public class RitualSlimeActiveTargetGoal<T extends LivingEntity> extends RitualActiveTargetGoal<T> {
    public RitualSlimeActiveTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    public boolean canStart() {
        if (((MobEntityMixinAccess) mob).rituals$isRitualMob()) {
            targetPredicate = TargetPredicate.createAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(null);
        }

        return super.canStart();
    }
}
