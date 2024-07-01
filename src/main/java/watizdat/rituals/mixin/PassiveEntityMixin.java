package watizdat.rituals.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import watizdat.rituals.access.PassiveEntityMixinAccess;
import watizdat.rituals.state.ModDataAttachments;

@Mixin(PassiveEntity.class)
public abstract class PassiveEntityMixin extends PathAwareEntity implements PassiveEntityMixinAccess {
    protected PassiveEntityMixin(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

//    protected void initGoals() {
//        goalSelector.add(6, new LookAroundGoal(this));
//        goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
//        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
//        goalSelector.add(4, new MeleeAttackGoal(this, 1d, false));
//        targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
//    }

    @Override
    public void rituals$addAttackGoals(World world) {
        getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier(
                "Entity attack damage",
                getMaxHealth() / 2,
                EntityAttributeModifier.Operation.ADDITION
        ));

        if (world != null && !world.isClient) {
            goalSelector.clear(goal -> true);

            goalSelector.add(101, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
            goalSelector.add(101, new LookAroundGoal(this));
            goalSelector.add(100, new MeleeAttackGoal(this, 1d, false));
            targetSelector.add(100, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        }
    }

//    @Inject(at = @At("TAIL"), method = "<init>")
//    private void rituals$init(CallbackInfo info, @Local(argsOnly = true) World world) {
//        getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier(
//                "Entity attack damage",
//                getMaxHealth() / 2,
//                EntityAttributeModifier.Operation.ADDITION
//        ));
//
//        if (world != null && !world.isClient && hasAttached(ModDataAttachments.getRitualPolePosPersistent())) {
//            goalSelector.add(101, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
//            goalSelector.add(101, new LookAroundGoal(this));
//            goalSelector.add(100, new MeleeAttackGoal(this, 1d, false));
//            targetSelector.add(100, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
//        }
//    }
}
