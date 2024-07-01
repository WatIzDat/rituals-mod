package watizdat.rituals.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import watizdat.rituals.Rituals;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> OUTSIDE_RITUAL_CIRCLE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE, Rituals.id("outside_ritual_circle"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
