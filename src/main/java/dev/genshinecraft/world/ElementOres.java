package dev.genshinecraft.world;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/** Seven mineable ore blocks; their loot tables drop existing elemental sigils. */
public final class ElementOres {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.BLOCK);
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ITEM);

    public static final Map<CarrierData.Element, RegistrySupplier<Block>> ORES = registerOres("ore_", 3.0f);
    public static final Map<CarrierData.Element, RegistrySupplier<Block>> DEEPSLATE_ORES =
            registerOres("deepslate_ore_", 4.5f);
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> ORE_ITEMS =
            registerItems("ore_", ORES);
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> DEEPSLATE_ORE_ITEMS =
            registerItems("deepslate_ore_", DEEPSLATE_ORES);

    private ElementOres() {
    }

    private static Map<CarrierData.Element, RegistrySupplier<Block>> registerOres(String prefix, float strength) {
        Map<CarrierData.Element, RegistrySupplier<Block>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = prefix + element.name().toLowerCase(Locale.ROOT);
            result.put(element, BLOCKS.register(name, () -> {
                BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                        .strength(strength, 3.0f).requiresCorrectToolForDrops().sound(SoundType.STONE);
                //? if >=26.3 {
                /*properties.setId(ResourceKey.create(Registries.BLOCK,
                        Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
                *///?}
                return new Block(properties);
            }));
        }
        return result;
    }

    private static Map<CarrierData.Element, RegistrySupplier<Item>> registerItems(
            String prefix, Map<CarrierData.Element, RegistrySupplier<Block>> blocks) {
        Map<CarrierData.Element, RegistrySupplier<Item>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = prefix + element.name().toLowerCase(Locale.ROOT);
            result.put(element, ITEMS.register(name, () -> {
                Item.Properties properties = new Item.Properties();
                //? if >=26.3 {
                /*properties.setId(ResourceKey.create(Registries.ITEM,
                        Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
                *///?}
                return new BlockItem(blocks.get(element).get(), properties);
            }));
        }
        return result;
    }

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
    }
}
