package watizdat.rituals.entity.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class RitualStatusEffect extends StatusEffect {
    public RitualStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF2D00);

        addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "54ab05e0-381e-4121-a0ec-f11032419b35",
                8d,
                EntityAttributeModifier.Operation.ADDITION);

        addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "a6443e8a-1f4e-443d-ad09-4b0f74c0264e",
                1.6d,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

        addAttributeModifier(
                EntityAttributes.GENERIC_FLYING_SPEED,
                "87d2e46e-31e1-4f0c-8327-37fc37accf43",
                1.6d,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
