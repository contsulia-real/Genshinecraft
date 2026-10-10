package dev.genshinecraft.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CarrierItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<Item> VISION = register("vision");
    public static final RegistrySupplier<Item> MOON_WHEEL = register("moon_wheel");
    public static final RegistrySupplier<Item> STAR_WEDGE = register("star_wedge");
    public static final RegistrySupplier<CreativeModeTab> CARRIER_TAB = TABS.register("carriers", () ->
            CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.genshinecraft"))
                    .icon(() -> new ItemStack(VISION.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(VISION.get());
                        output.accept(MOON_WHEEL.get());
                        output.accept(STAR_WEDGE.get());
                    }))
    );

    private CarrierItems() {
    }

    private static RegistrySupplier<Item> register(String name) {
        return ITEMS.register(name, () -> {
            Item.Properties properties = new Item.Properties().stacksTo(1).fireResistant();
            //? if >=26.3 {
            /*properties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
            *///?}
            return new Item(properties);
        });
    }

    public static boolean isCarrier(ItemStack stack) {
        return isCarrier(stack.getItem());
    }

    public static boolean isCarrier(Item item) {
        return item == VISION.get() || item == MOON_WHEEL.get() || item == STAR_WEDGE.get();
    }

    public static void init() {
        ITEMS.register();
        TABS.register();
    }
}
