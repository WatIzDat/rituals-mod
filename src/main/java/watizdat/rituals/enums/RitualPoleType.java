package watizdat.rituals.enums;

import net.minecraft.util.StringIdentifiable;

public enum RitualPoleType implements StringIdentifiable {
    OVERWORLD("overworld"),
    AQUATIC("aquatic"),
    NETHER("nether"),
    END("end");

    public static final StringIdentifiable.Codec<RitualPoleType> CODEC = StringIdentifiable.createCodec(RitualPoleType::values);

    private final String name;

    RitualPoleType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
