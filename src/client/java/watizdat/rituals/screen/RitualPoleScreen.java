package watizdat.rituals.screen;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import watizdat.rituals.Rituals;
import watizdat.rituals.network.ModNetworkConstants;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RitualPoleScreen extends BaseOwoScreen<FlowLayout> {
    private final List<Identifier> entityTypesKilled;
    private final BlockPos pos;

    public RitualPoleScreen(List<Identifier> entityTypesKilled, BlockPos pos) {
        this.entityTypesKilled = entityTypesKilled;
        this.pos = pos;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        FlowLayout container = Containers.verticalFlow(Sizing.fixed(300), Sizing.content());

        container
                .padding(Insets.of(10))
                .surface(Surface.DARK_PANEL)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        container
                .child(
                        Components.label(Text.translatable("gui.rituals.ritual_pole.title"))
                                .margins(Insets.bottom(10))
                );

        container
                .child(
                        Components.label(Text.translatable("gui.rituals.ritual_pole.mobs_killed_message"))
                                    .margins(Insets.bottom(10))
                );

        ScrollContainer<FlowLayout> scrollContainer = Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(150), Components.list(entityTypesKilled, flowLayout -> {
            flowLayout.horizontalAlignment(HorizontalAlignment.CENTER);
            flowLayout.verticalAlignment(VerticalAlignment.CENTER);
        }, identifier -> {
            FlowLayout entry = Containers.horizontalFlow(Sizing.fill(100), Sizing.content(40));

            entry.horizontalAlignment(HorizontalAlignment.CENTER);
            entry.verticalAlignment(VerticalAlignment.CENTER);

            entry.child(Components.entity(Sizing.fixed(25), Registries.ENTITY_TYPE.get(identifier), null)
                    .margins(Insets.right(40)));
            entry.child(Components.label(Registries.ENTITY_TYPE.get(identifier).getName()));

            return entry;
        }, true));

        scrollContainer.scrollbarThiccness(5);
        scrollContainer.fixedScrollbarLength(0);

        scrollContainer.surface(Surface.VANILLA_TRANSLUCENT);
        scrollContainer.margins(Insets.bottom(10));

        container.child(scrollContainer);

        container
                .child(
                        Components.label(Text.translatable("gui.rituals.ritual_pole.revenge_warning_message"))
                                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                                .horizontalSizing(Sizing.fill(100))
                                .margins(Insets.bottom(10))
                );

        FlowLayout buttonContainer = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());

        buttonContainer
                .horizontalAlignment(HorizontalAlignment.RIGHT);

        buttonContainer
                .child(Components.button(Text.translatable("gui.rituals.ritual_pole.cancel_button"), button -> {
                    client.setScreen(null);
                }))
                .child(Components.button(Text.translatable("gui.rituals.ritual_pole.start_ritual_button"), button -> {
                    client.setScreen(null);

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);

                    ClientPlayNetworking.send(ModNetworkConstants.START_RITUAL_PACKET_ID, buf);
                }).margins(Insets.left(10)));

        container.child(buttonContainer);

        rootComponent.child(container);
    }
}
