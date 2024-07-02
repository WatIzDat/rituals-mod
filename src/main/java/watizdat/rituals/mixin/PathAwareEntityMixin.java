package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import watizdat.rituals.access.PathAwareEntityMixinAccess;
import watizdat.rituals.entity.goal.MoveToRitualPoleGoal;

@Mixin(PathAwareEntity.class)
public abstract class PathAwareEntityMixin extends MobEntity implements PathAwareEntityMixinAccess {
    protected PathAwareEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void rituals$addGeneralGoals() {
        System.out.println("test");
        goalSelector.add(102, new MoveToRitualPoleGoal((PathAwareEntity) (Object) this, 1.1, 10));
    }
}
