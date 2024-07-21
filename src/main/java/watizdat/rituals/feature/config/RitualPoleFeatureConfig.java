package watizdat.rituals.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import watizdat.rituals.enums.RitualPoleType;

public record RitualPoleFeatureConfig(RitualPoleType type) implements FeatureConfig {
    public static final Codec<RitualPoleFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RitualPoleType.CODEC.fieldOf("ritual_type").forGetter(RitualPoleFeatureConfig::type)
            ).apply(instance, RitualPoleFeatureConfig::new));
}
