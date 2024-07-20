package watizdat.rituals.screen;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import watizdat.rituals.Rituals;
import watizdat.rituals.block.entity.RitualPoleBlockEntity;
import watizdat.rituals.enums.RitualState;
import watizdat.rituals.network.ModNetworkConstants;

import java.util.List;
import java.util.Map;

public class RitualPoleUIModelScreen extends BaseUIModelScreen<FlowLayout> {
    private RitualState ritualState;
    private final List<Identifier> entityTypesKilled;
    private final BlockPos pos;

    public RitualPoleUIModelScreen(RitualState ritualState, List<Identifier> entityTypesKilled, BlockPos pos) {
        super(FlowLayout.class, DataSource.asset(Rituals.id("ritual_pole_ui_model")));

        this.ritualState = ritualState;
        this.entityTypesKilled = entityTypesKilled;
        this.pos = pos;
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
        for (Identifier entityTypeId : entityTypesKilled) {
            FlowLayout entityTypesKilledListContainer =
                    rootComponent.childById(FlowLayout.class, "entity-types-killed-list-container");

            GridLayout entityTypesKilledListEntry = model.expandTemplate(
                    GridLayout.class,
                    "entity-types-killed-list-entry",
                    Map.of(
                            "entityType", entityTypeId.toString(),
                            "entityTypeName", Registries.ENTITY_TYPE.get(entityTypeId).getName().getString()));

            FlowLayout entityTypeLoot = entityTypesKilledListEntry.childById(FlowLayout.class, "entity-type-loot");

            for (ItemStack itemStack : RitualPoleBlockEntity.ENTITY_TYPE_LOOT_MAP.get(Registries.ENTITY_TYPE.get(entityTypeId))) {
                ItemComponent itemComponent = model.expandTemplate(
                        ItemComponent.class,
                        "entity-type-loot-item",
                        Map.of("item", Registries.ITEM.getId(itemStack.getItem()).toString()));

                entityTypeLoot.child(itemComponent);
            }

            entityTypesKilledListContainer.child(entityTypesKilledListEntry);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
