package dev.genshinecraft.world;

import dev.architectury.event.events.common.LootEvent;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.nbt.CompoundTag;
//? if =1.20.1 {
/*import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
*///?} else {
import net.minecraft.world.level.storage.loot.functions.SetCustomDataFunction;
//?}
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

/** Adds a small independent sigil pool to existing chest loot tables without replacing vanilla loot. */
public final class ChestSigilLoot {
    private ChestSigilLoot() {}

    public static void init() {
        //? if =1.20.1 {
        /*LootEvent.MODIFY_LOOT_TABLE.register((table, key, context, builtin) -> {
            String path = key.getPath();
        *///?} else if >=26.3 {
        /*LootEvent.MODIFY_LOOT_TABLE.register((registries, key, context, builtin) -> {
            String path = key.identifier().getPath();
        *///?} else {
        LootEvent.MODIFY_LOOT_TABLE.register((key, context, builtin) -> {
            String path = key.location().getPath();
        //?}
            if (!builtin || !path.startsWith("chests/")) return;
            LootPool.Builder pool = LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.75f));
            for (CarrierData.Element element : CarrierData.Element.values()) {
                pool.add(LootItem.lootTableItem(CarrierItems.SIGILS.get(element).get()));
            }
            pool.add(LootItem.lootTableItem(CarrierItems.MOON_SIGIL.get()));
            context.addPool(pool);
            LootPool.Builder organs = LootPool.lootPool()
                    .when(LootItemRandomChanceCondition.randomChance(0.05f));
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(CarrierData.UNCLAIMABLE, true);
            //? if =1.20.1 {
            /*LootItemFunction.Builder unclaimable = SetNbtFunction.setTag(tag);
            *///?} else {
            LootItemFunction.Builder unclaimable = SetCustomDataFunction.setCustomData(tag);
            //?}
            for (CarrierData.Element element : CarrierData.Element.values()) {
                organs.add(LootItem.lootTableItem(CarrierItems.VISIONS.get(element).get()).apply(unclaimable));
                for (var region : CarrierItems.REGIONAL_VISIONS.values()) {
                    organs.add(LootItem.lootTableItem(region.get(element).get()).apply(unclaimable));
                }
                organs.add(LootItem.lootTableItem(CarrierItems.MOON_WHEELS.get(element).get()).apply(unclaimable));
                organs.add(LootItem.lootTableItem(CarrierItems.STAR_WEDGES.get(element).get()).apply(unclaimable));
            }
            context.addPool(organs);
        });
    }
}
