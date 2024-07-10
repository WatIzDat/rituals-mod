package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import watizdat.rituals.access.PathAwareEntityMixinAccess;
import watizdat.rituals.entity.goal.MoveToRitualPoleGoal;

@Mixin(PathAwareEntity.class)
public abstract class PathAwareEntityMixin extends MobEntity implements PathAwareEntityMixinAccess {
    protected PathAwareEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

//    @Override
//    public void rituals$addPathAwareGoals(World world) {
////        getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier(
////                "Entity attack damage",
////                getMaxHealth() / 2,
////                EntityAttributeModifier.Operation.ADDITION
////        ));
//
//        if (world != null && !world.isClient) {
//            getBrain().clear();
//
//            goalSelector.clear(goal -> true);
//            targetSelector.clear(goal -> true);
//
//            goalSelector.add(3, new MoveToRitualPoleGoal((PathAwareEntity) (Object) this, 1.1, 50));
//            goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
//            goalSelector.add(2, new LookAroundGoal(this));
//            goalSelector.add(1, new MeleeAttackGoal((PathAwareEntity) (Object) this, 1d, false));
//            targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
//        }
//    }
}
