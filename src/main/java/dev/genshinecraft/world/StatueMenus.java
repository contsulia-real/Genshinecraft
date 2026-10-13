package dev.genshinecraft.world;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public final class StatueMenus {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Genshinecraft.MOD_ID, Registries.MENU);

    //? if >=26.3 {
    /*public static final RegistrySupplier<MenuType<StatueOfferingMenu>> TYPE =
            MENUS.register("statue_offering", () -> MenuRegistry.ofExtended(
                    (id, inventory, unused) -> new StatueOfferingMenu(id, inventory)));
    *///?} else {
    public static final RegistrySupplier<MenuType<StatueOfferingMenu>> TYPE =
            MENUS.register("statue_offering", () -> MenuRegistry.of(StatueOfferingMenu::new));
    //?}

    private StatueMenus() {}

    public static void init() {
        MENUS.register();
    }
}
