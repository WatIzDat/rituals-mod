package watizdat.rituals.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.access.MobEntityMixinAccess;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.init.ModStatusEffects;
import watizdat.rituals.state.ModComponents;

public class ModEntityHelper {
    public static void setAsRitualMob(Entity entity, BlockPos ritualPolePos) {
        ModComponents.RITUAL_POLE_POS_COMPONENT.get(entity).set(ritualPolePos);

        ((MobEntityMixinAccess) entity).rituals$setAsRitualMob();

        StatusEffectInstance ritualStatusEffect = new StatusEffectInstance(
                ModStatusEffects.RITUAL_STATUS_EFFECT, StatusEffectInstance.INFINITE, 0, false, true);
        ((LivingEntity) entity).addStatusEffect(ritualStatusEffect);

        StatusEffectInstance glowingStatusEffect = new StatusEffectInstance(
                StatusEffects.GLOWING, StatusEffectInstance.INFINITE, 0, false, false);
        ((LivingEntity) entity).addStatusEffect(glowingStatusEffect);

        ((RitualPoleBlockEntity) entity.getWorld().getBlockEntity(ritualPolePos)).addEntityUuid(entity.getUuid());
    }
}
