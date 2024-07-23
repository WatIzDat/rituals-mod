package watizdat.rituals.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.Rituals;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.enums.RitualPoleType;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.network.ModNetworkConstants;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class RitualPoleUIModelScreen extends BaseUIModelScreen<FlowLayout> {
    private RitualState ritualState;
    private final List<Map.Entry<Identifier, Integer>> entityTypesKilled;
    private final HashMap<Identifier, Integer> availableCounts;
    private final HashMap<Identifier, Integer> usedCounts;
    private final BlockPos pos;
    private final RitualPoleType ritualPoleType;

    public RitualPoleUIModelScreen(RitualState ritualState, List<Map.Entry<Identifier, Integer>> entityTypesKilled, BlockPos pos, RitualPoleType ritualPoleType) {
        super(FlowLayout.class, DataSource.asset(Rituals.id("ritual_pole_ui_model")));

        this.ritualState = ritualState;
        this.entityTypesKilled = entityTypesKilled;
        this.pos = pos;
        this.ritualPoleType = ritualPoleType;

        this.availableCounts = new HashMap<>(entityTypesKilled.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        this.usedCounts = new HashMap<>(entityTypesKilled.stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> 0)));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        initButtons(rootComponent);

        initLists(rootComponent);
    }

    private void initButtons(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "cancel-button").onPress(button -> client.setScreen(null));

        rootComponent.childById(ButtonComponent.class, "start-ritual-button").onPress(button -> {
            client.setScreen(null);

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);

            ClientPlayNetworking.send(ModNetworkConstants.START_RITUAL_PACKET_ID, buf);
        });
    }

    private void initLists(FlowLayout rootComponent) {
        FlowLayout entityTypesKilledListContainer =
                rootComponent.childById(FlowLayout.class, "entity-types-killed-list-container");

        entityTypesKilledListContainer.clearChildren();

        entityTypesKilled.sort((entry1, entry2) -> {
            EntityType<?> entityType1 = Registries.ENTITY_TYPE.get(entry1.getKey());
            EntityType<?> entityType2 = Registries.ENTITY_TYPE.get(entry2.getKey());

            return Collator.getInstance(new Locale(MinecraftClient.getInstance().options.language))
                    .compare(entityType1.getName().getString(), entityType2.getName().getString());
        });

        List<Component> validEntries = new ArrayList<>();
        List<Component> invalidEntries = new ArrayList<>();

        for (int i = 0; i < entityTypesKilled.size(); i++) {
            Map.Entry<Identifier, Integer> entry = entityTypesKilled.get(i);
            Identifier entityTypeId = entry.getKey();
            int availableCount = availableCounts.get(entityTypeId);
            int usedCount = usedCounts.get(entityTypeId);

            boolean isEntityValidForRitualPoleType =
                    RitualPoleBlockEntity.isEntityValidForRitualPoleType(Registries.ENTITY_TYPE.get(entityTypeId), ritualPoleType);

            FlowLayout entityTypesKilledListEntry = model.expandTemplate(
                    FlowLayout.class,
                    "entity-types-killed-list-entry",
                    Map.of(
                            "entityType", entityTypeId.toString(),
                            "entityTypeName", Registries.ENTITY_TYPE.get(entityTypeId).getName().getString(),
                            "availableCount", String.valueOf(availableCount),
                            "usedCount", String.valueOf(usedCount),
                            "color", isEntityValidForRitualPoleType ? "#00000000" : "#80FF2D00"));

            entityTypesKilledListEntry.childById(ButtonComponent.class, "add-entity-button").onPress(button -> {
                if (availableCounts.get(entityTypeId) == 0) {
                    return;
                }

                availableCounts.put(entityTypeId, availableCounts.get(entityTypeId) - 1);
                usedCounts.put(entityTypeId, usedCounts.get(entityTypeId) + 1);

                button.parent().parent()
                        .childById(LabelComponent.class, "availableCountLabel")
                        .text(Text.literal("Available: " + availableCounts.get(entityTypeId)));

                button.parent().parent()
                        .childById(LabelComponent.class, "usedCountLabel")
                        .text(Text.literal("Used: " + usedCounts.get(entityTypeId)));
            });

            entityTypesKilledListEntry.childById(ButtonComponent.class, "remove-entity-button").onPress(button -> {
                if (usedCounts.get(entityTypeId) == 0) {
                    return;
                }

                availableCounts.put(entityTypeId, availableCounts.get(entityTypeId) + 1);
                usedCounts.put(entityTypeId, usedCounts.get(entityTypeId) - 1);

                button.parent().parent()
                        .childById(LabelComponent.class, "availableCountLabel")
                        .text(Text.literal("Available: " + availableCounts.get(entityTypeId)));

                button.parent().parent()
                        .childById(LabelComponent.class, "usedCountLabel")
                        .text(Text.literal("Used: " + usedCounts.get(entityTypeId)));
            });

            FlowLayout entityTypeLoot = entityTypesKilledListEntry.childById(FlowLayout.class, "entity-type-loot");

            for (ItemStack itemStack : RitualPoleBlockEntity.ENTITY_TYPE_LOOT_MAP.get(Registries.ENTITY_TYPE.get(entityTypeId))) {
                FlowLayout itemComponent = model.expandTemplate(
                        FlowLayout.class,
                        "entity-type-loot-item",
                        Map.of(
                                "item", Registries.ITEM.getId(itemStack.getItem()).toString(),
                                "count", String.valueOf(itemStack.getCount())));

                entityTypeLoot.child(itemComponent);
            }

            (isEntityValidForRitualPoleType ? validEntries : invalidEntries).add(entityTypesKilledListEntry);
        }

        for (Component entry : validEntries) {
            entityTypesKilledListContainer.child(entry);
        }

        for (Component entry : invalidEntries) {
            entityTypesKilledListContainer.child(entry);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
