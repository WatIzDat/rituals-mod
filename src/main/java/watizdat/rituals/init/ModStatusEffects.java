package watizdat.rituals.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import watizdat.rituals.Rituals;
import watizdat.rituals.entity.effect.RitualStatusEffect;

public class ModStatusEffects {
    public static final StatusEffect RITUAL_STATUS_EFFECT = new RitualStatusEffect();

    public static void init() {
        Registry.register(
                Registries.STATUS_EFFECT, Rituals.id("ritual_status_effect"), RITUAL_STATUS_EFFECT);
    }
}
