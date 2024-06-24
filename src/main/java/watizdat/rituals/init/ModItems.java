package watizdat.rituals.init;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import watizdat.rituals.Rituals;
import watizdat.rituals.item.RitualTokenItem;

public class ModItems {
    public static final Item RITUAL_TOKEN = new RitualTokenItem(new FabricItemSettings());

    public static void init() {
        Registry.register(Registries.ITEM, Rituals.id("ritual_token"), RITUAL_TOKEN);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(RITUAL_TOKEN);
        });
    }
}
