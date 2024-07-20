package watizdat.rituals.init;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import watizdat.rituals.state.ModComponents;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class ModEvents {
    public static void init() {
        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) -> {
            if (entity.isPlayer()) {
                PlayerEntity player = (PlayerEntity) entity;

                player.sendMessage(Text.translatable("block.rituals.bed.message"), true);
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if (entity.isPlayer() && !entity.getWorld().isClient && ((PlayerEntity) entity).canResetTimeBySleeping()) {
                ModComponents.ENTITY_TYPES_KILLED_COMPONENT.get(entity).clear();
            }
        });

//        LootTableEvents.ALL_LOADED.register((resourceManager, lootManager) -> {
//            for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
//                LootTable lootTable = lootManager.getLootTable(entityType.getLootTableId());
//
//                lootTable.
//
//                for (LootPool pool : lootTable.pools) {
//                    for (LootPoolEntry entry : pool.entries) {
//
//                    }
//                }
//            }
//        });
    }
}
