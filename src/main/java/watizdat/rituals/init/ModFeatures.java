package watizdat.rituals.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import watizdat.rituals.Rituals;
import watizdat.rituals.feature.RitualPoleFeature;

public class ModFeatures {
    public static final Identifier RITUAL_POLE_ID = Rituals.id("ritual_pole");
    public static final RitualPoleFeature RITUAL_POLE = new RitualPoleFeature(DefaultFeatureConfig.CODEC);

    public static void init() {
        Registry.register(Registries.FEATURE, RITUAL_POLE_ID, RITUAL_POLE);

        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, RITUAL_POLE_ID)
        );
    }
}
