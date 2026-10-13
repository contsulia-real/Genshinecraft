package dev.genshinecraft.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}

import java.util.EnumMap;
import java.util.Map;
import java.util.Locale;

public final class StatueBlocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ITEM);
    public static final Map<CarrierData.Element, RegistrySupplier<Block>> ALTARS = register();
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> ALTAR_ITEMS = registerItems();

    public static final RegistrySupplier<Block> MOON_ALTAR = BLOCKS.register("statue_altar_moon", () -> {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .strength(-1.0f, 3600000.0f).noLootTable().lightLevel(state -> 12);
        //? if >=26.3 {
        /*properties.setId(ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "statue_altar_moon")));
        *///?}
        return new MoonStatueCoreBlock(properties);
    });
    public static final RegistrySupplier<Item> MOON_ALTAR_ITEM = ITEMS.register("statue_altar_moon", () -> {
        Item.Properties properties = new Item.Properties();
        //? if >=26.3 {
        /*properties.setId(ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "statue_altar_moon")));
        *///?}
        return new BlockItem(MOON_ALTAR.get(), properties);
    });

    private StatueBlocks() {
    }

    private static Map<CarrierData.Element, RegistrySupplier<Block>> register() {
        Map<CarrierData.Element, RegistrySupplier<Block>> blocks =
                new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = "statue_altar_" + element.name().toLowerCase(Locale.ROOT);
            blocks.put(element, BLOCKS.register(name, () -> {
                BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                        .strength(-1.0f, 3600000.0f)
                        .noLootTable()
                        .lightLevel(state -> 8);
                //? if >=26.3 {
                /*properties.setId(ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
                *///?}
                return new StatueCoreBlock(properties, element);
            }));
        }
        return blocks;
    }

    private static Map<CarrierData.Element, RegistrySupplier<Item>> registerItems() {
        Map<CarrierData.Element, RegistrySupplier<Item>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = "statue_altar_" + element.name().toLowerCase(Locale.ROOT);
            result.put(element, ITEMS.register(name, () -> {
                Item.Properties properties = new Item.Properties();
                //? if >=26.3 {
                /*properties.setId(ResourceKey.create(Registries.ITEM,
                        Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
                *///?}
                return new BlockItem(ALTARS.get(element).get(), properties);
            }));
        }
        return result;
    }

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
    }
}
