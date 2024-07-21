package watizdat.rituals.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import watizdat.rituals.Rituals;
import watizdat.rituals.feature.RitualPoleFeature;
import watizdat.rituals.feature.config.RitualPoleFeatureConfig;

public class ModFeatures {
    public static final RitualPoleFeature RITUAL_POLE = new RitualPoleFeature(RitualPoleFeatureConfig.CODEC);
    public static final RitualPoleFeature AQUATIC_RITUAL_POLE = new RitualPoleFeature(RitualPoleFeatureConfig.CODEC);
    public static final RitualPoleFeature NETHER_RITUAL_POLE = new RitualPoleFeature(RitualPoleFeatureConfig.CODEC);
    public static final RitualPoleFeature END_RITUAL_POLE = new RitualPoleFeature(RitualPoleFeatureConfig.CODEC);

    public static final Identifier RITUAL_POLE_ID = Rituals.id("ritual_pole");
    public static final Identifier AQUATIC_RITUAL_POLE_ID = Rituals.id("aquatic_ritual_pole");
    public static final Identifier NETHER_RITUAL_POLE_ID = Rituals.id("nether_ritual_pole");
    public static final Identifier END_RITUAL_POLE_ID = Rituals.id("end_ritual_pole");

    public static void init() {
        Registry.register(Registries.FEATURE, RITUAL_POLE_ID, RITUAL_POLE);
        Registry.register(Registries.FEATURE, AQUATIC_RITUAL_POLE_ID, AQUATIC_RITUAL_POLE);
        Registry.register(Registries.FEATURE, NETHER_RITUAL_POLE_ID, NETHER_RITUAL_POLE);
        Registry.register(Registries.FEATURE, END_RITUAL_POLE_ID, END_RITUAL_POLE);

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld().and(BiomeSelectors.tag(ConventionalBiomeTags.OCEAN).negate()),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, RITUAL_POLE_ID)
        );

        BiomeModifications.addFeature(
                BiomeSelectors.tag(ConventionalBiomeTags.OCEAN),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, AQUATIC_RITUAL_POLE_ID)
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheNether(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, NETHER_RITUAL_POLE_ID)
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInTheEnd(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                RegistryKey.of(RegistryKeys.PLACED_FEATURE, END_RITUAL_POLE_ID)
        );
    }
}
