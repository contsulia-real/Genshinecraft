package dev.genshinecraft.item;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import net.minecraft.core.registries.Registries;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CarrierItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> VISION = register("vision");
    public static final RegistrySupplier<Item> MOON_WHEEL = register("moon_wheel");
    public static final RegistrySupplier<Item> STAR_WEDGE = register("star_wedge");

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
    }
}
