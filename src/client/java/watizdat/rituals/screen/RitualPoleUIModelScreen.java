package watizdat.rituals.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.Rituals;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.enums.RitualPoleType;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.network.ModNetworkConstants;

import java.text.Collator;
import java.util.*;

public class RitualPoleUIModelScreen extends BaseUIModelScreen<FlowLayout> {
    private RitualState ritualState;
    private final List<Identifier> entityTypesKilled;
    private final BlockPos pos;
    private final RitualPoleType ritualPoleType;

    public RitualPoleUIModelScreen(RitualState ritualState, List<Identifier> entityTypesKilled, BlockPos pos, RitualPoleType ritualPoleType) {
        super(FlowLayout.class, DataSource.asset(Rituals.id("ritual_pole_ui_model")));

        this.ritualState = ritualState;
        this.entityTypesKilled = entityTypesKilled;
        this.pos = pos;
        this.ritualPoleType = ritualPoleType;
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
        entityTypesKilled.sort((id1, id2) -> {
            EntityType<?> entityType1 = Registries.ENTITY_TYPE.get(id1);
            EntityType<?> entityType2 = Registries.ENTITY_TYPE.get(id2);

            return Collator.getInstance(new Locale(MinecraftClient.getInstance().options.language))
                    .compare(entityType1.getName().getString(), entityType2.getName().getString());
        });

        List<Component> validEntries = new ArrayList<>();
        List<Component> invalidEntries = new ArrayList<>();

        FlowLayout entityTypesKilledListContainer =
                rootComponent.childById(FlowLayout.class, "entity-types-killed-list-container");

        for (Identifier entityTypeId : entityTypesKilled) {
            boolean isEntityValidForRitualPoleType =
                    RitualPoleBlockEntity.isEntityValidForRitualPoleType(Registries.ENTITY_TYPE.get(entityTypeId), ritualPoleType);

            GridLayout entityTypesKilledListEntry = model.expandTemplate(
                    GridLayout.class,
                    "entity-types-killed-list-entry",
                    Map.of(
                            "entityType", entityTypeId.toString(),
                            "entityTypeName", Registries.ENTITY_TYPE.get(entityTypeId).getName().getString(),
                            "color", isEntityValidForRitualPoleType ? "#00000000" : "#80FF2D00"));

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
