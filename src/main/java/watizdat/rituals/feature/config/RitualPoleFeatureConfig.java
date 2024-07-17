package watizdat.rituals.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;

public record RitualPoleFeatureConfig(boolean isNetherPole) implements FeatureConfig {
    public static final Codec<RitualPoleFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("is_nether_pole").forGetter(RitualPoleFeatureConfig::isNetherPole)
            ).apply(instance, RitualPoleFeatureConfig::new));
}
