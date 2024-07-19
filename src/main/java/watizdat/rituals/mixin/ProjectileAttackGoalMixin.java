package watizdat.rituals.mixin;

import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import watizdat.rituals.access.ProjectileAttackGoalMixinAccess;

@Mixin(ProjectileAttackGoal.class)
public class ProjectileAttackGoalMixin implements ProjectileAttackGoalMixinAccess {
    @Shadow @Final @Mutable
    private int maxIntervalTicks;

    @Shadow @Final @Mutable
    private int minIntervalTicks;

    @Override
    public void rituals$setIntervalTicks(int intervalTicks) {
        minIntervalTicks = intervalTicks;
        maxIntervalTicks = intervalTicks;
    }
}
