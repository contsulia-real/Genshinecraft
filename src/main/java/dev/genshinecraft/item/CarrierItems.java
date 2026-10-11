package dev.genshinecraft.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.genshinecraft.Genshinecraft;
import net.minecraft.core.registries.Registries;
//? if >=1.21.1 {
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
*///?}

import java.util.EnumMap;
import java.util.Map;

public final class CarrierItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.ITEM);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.CREATIVE_MODE_TAB);
    //? if >=1.21.1 {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Genshinecraft.MOD_ID, Registries.DATA_COMPONENT_TYPE);
    private static final RegistrySupplier<DataComponentType<?>> CARRIER_DATA = COMPONENTS.register("carrier_data", () ->
            DataComponentType.<CarrierData>builder()
                    .persistent(CarrierData.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodec(CarrierData.CODEC))
                    .build());
    private static final RegistrySupplier<DataComponentType<?>> WEAPON_BINDING = COMPONENTS.register("weapon_binding", () ->
            DataComponentType.<WeaponBinding>builder()
                    .persistent(WeaponBinding.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodec(WeaponBinding.CODEC))
                    .build());
    //?}

    public static final Map<CarrierData.Element, RegistrySupplier<Item>> VISIONS = registerElements("vision");
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> MOON_WHEELS = registerElements("moon_wheel");
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> STAR_WEDGES = registerElements("star_wedge");
    public static final Map<CarrierData.Element, RegistrySupplier<Item>> SIGILS = registerSigils();
    public static final RegistrySupplier<Item> MOON_SIGIL = ITEMS.register("moon_sigil", () -> new Item(sigilProperties("moon_sigil")));

    public static final RegistrySupplier<CreativeModeTab> CARRIER_TAB = TABS.register("carriers", () ->
            CreativeTabRegistry.create(builder -> builder
                    .title(Component.translatable("itemGroup.genshinecraft"))
                    .icon(() -> new ItemStack(VISIONS.get(CarrierData.Element.ANEMO).get()))
                    .displayItems((parameters, output) -> {
                        for (CarrierData.Element element : CarrierData.Element.values()) {
                            output.accept(VISIONS.get(element).get());
                            output.accept(MOON_WHEELS.get(element).get());
                            output.accept(STAR_WEDGES.get(element).get());
                            output.accept(SIGILS.get(element).get());
                        }
                        output.accept(MOON_SIGIL.get());
                    }))
    );

    private CarrierItems() {
    }

    private static Map<CarrierData.Element, RegistrySupplier<Item>> registerElements(String kind) {
        Map<CarrierData.Element, RegistrySupplier<Item>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String name = kind + "_" + element.name().toLowerCase(java.util.Locale.ROOT);
            result.put(element, ITEMS.register(name, () -> new ElementCarrierItem(properties(name, element), element, kind)));
        }
        return result;
    }

    private static Map<CarrierData.Element, RegistrySupplier<Item>> registerSigils() {
        Map<CarrierData.Element, RegistrySupplier<Item>> result = new EnumMap<>(CarrierData.Element.class);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            String id = element.name().toLowerCase(java.util.Locale.ROOT) + "_sigil";
            result.put(element, ITEMS.register(id, () -> new Item(sigilProperties(id))));
        }
        return result;
    }

    private static Item.Properties sigilProperties(String name) {
        Item.Properties properties = new Item.Properties();
        //? if >=26.3 {
        /*properties.setId(ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, name)));
        *///?}
        return properties;
    }

    private static Item.Properties properties(String name, CarrierData.Element element) {
        Item.Properties properties = new Item.Properties().stacksTo(1).fireResistant();
        //? if >=1.21.1 {
        properties.component(DataComponents.ITEM_NAME,
                Component.translatable("item." + Genshinecraft.MOD_ID + "." + name)
                        .withStyle(style -> style.withColor(element.color())));
        //?}
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

    public static boolean isVision(ItemStack stack) {
        return stack.getItem() instanceof ElementCarrierItem carrier && carrier.kind.equals("vision");
    }

    //? if >=1.21.1 {
    @SuppressWarnings("unchecked")
    public static DataComponentType<CarrierData> carrierDataType() {
        return (DataComponentType<CarrierData>) CARRIER_DATA.get();
    }
    //?}

    //? if >=1.21.1 {
    @SuppressWarnings("unchecked")
    public static DataComponentType<WeaponBinding> weaponBindingType() {
        return (DataComponentType<WeaponBinding>) WEAPON_BINDING.get();
    }
    //?}

    public static void init() {
        //? if >=1.21.1 {
        COMPONENTS.register();
        //?}
        ITEMS.register();
        TABS.register();
    }

    private static final class ElementCarrierItem extends Item {
        private final CarrierData.Element element;
        private final String kind;

        private ElementCarrierItem(Properties properties, CarrierData.Element element, String kind) {
            super(properties);
            this.element = element;
            this.kind = kind;
        }

        //? if =1.20.1 {
        /*@Override
        public Component getName(ItemStack stack) {
            return Component.translatable(getDescriptionId())
                    .withStyle(style -> style.withColor(element.color()));
        }
        *///?}

        @Override
        public boolean overrideStackedOnOther(ItemStack carrier, Slot slot, ClickAction action, Player player) {
            if (action != ClickAction.SECONDARY || !slot.mayPickup(player)
                    || !WeaponBinding.isWeapon(slot.getItem())) {
                return false;
            }
            // Creative inventory clicks run locally; its inventory listener sends changed
            // slots to the server via the normal creative-mode item update packet.
            if ((player instanceof ServerPlayer || player.getAbilities().instabuild)
                    && WeaponBinding.bind(slot.getItem(), carrier, player)) {
                slot.setChanged();
            }
            return true;
        }

        @Override
        public boolean overrideOtherStackedOnMe(ItemStack carrier, ItemStack carried, Slot slot,
                                                 ClickAction action, Player player, SlotAccess access) {
            if (action != ClickAction.SECONDARY || !slot.mayPickup(player)
                    || !WeaponBinding.isWeapon(carried)) {
                return false;
            }
            if (player instanceof ServerPlayer || player.getAbilities().instabuild) {
                WeaponBinding.bind(carried, carrier, player);
            }
            return true;
        }
    }
}
