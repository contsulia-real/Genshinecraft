package dev.genshinecraft.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}

import java.util.EnumMap;
import java.util.Map;

public final class CarrierItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final Map<CarrierData.Element, RegistrySupplier<Item>> VISIONS = registerElements("vision");
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> MOON_WHEELS = registerElements("moon_wheel");
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> STAR_WEDGES = registerElements("star_wedge");

    public static final RegistrySupplier<CreativeModeTab> CARRIER_TAB = TABS.register("carriers", () ->
            CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.genshinecraft"))
                    .icon(() -> new ItemStack(VISIONS.get(CarrierData.Element.ANEMO).get()))
                    .displayItems((parameters, output) -> {
                        for (CarrierData.Element element : CarrierData.Element.values()) {
                            output.accept(VISIONS.get(element).get());
                            output.accept(MOON_WHEELS.get(element).get());
                            output.accept(STAR_WEDGES.get(element).get());
                        }
                    }))
    );

    private CarrierItems() {
    }

    private static Map<CarrierData.Element, RegistrySupplier<Item>> registerElements(String kind) {
        Map<CarrierData.Element, RegistrySupplier<Item>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = kind + "_" + element.name().toLowerCase(java.util.Locale.ROOT);
            result.put(element, ITEMS.register(name, () -> new ElementCarrierItem(properties(name), element)));
        }
        return result;
    }

    private static Item.Properties properties(String name) {
        Item.Properties properties = new Item.Properties().stacksTo(1).fireResistant();
        //? if >=26.3 {
        /*properties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
        *///?}
        return properties;
    }

    public static boolean isCarrier(ItemStack stack) {
        return isCarrier(stack.getItem());
    }

    public static boolean isCarrier(Item item) {
        return item instanceof ElementCarrierItem;
    }

    public static CarrierData.Element fixedElement(Item item) {
        return item instanceof ElementCarrierItem carrier ? carrier.element : null;
    }

    public static void init() {
        ITEMS.register();
        TABS.register();
    }

    private static final class ElementCarrierItem extends Item {
        private final CarrierData.Element element;

        private ElementCarrierItem(Properties properties, CarrierData.Element element) {
            super(properties);
            this.element = element;
        }
    }
}
