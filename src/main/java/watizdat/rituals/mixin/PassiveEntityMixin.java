package watizdat.rituals.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import watizdat.rituals.access.PassiveEntityMixinAccess;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements PassiveEntityMixinAccess {
    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void rituals$addAttackGoals(World world) {
        getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier(
                "Entity attack damage",
                getMaxHealth() / 2,
                EntityAttributeModifier.Operation.ADDITION
        ));

        if (world != null && !world.isClient) {
            goalSelector.clear(goal -> true);

            goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
            goalSelector.add(2, new LookAroundGoal(this));
            goalSelector.add(1, new MeleeAttackGoal(this, 1d, false));
            targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        }
    }
}
