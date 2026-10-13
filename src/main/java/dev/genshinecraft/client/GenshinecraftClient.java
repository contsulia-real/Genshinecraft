package dev.genshinecraft.client;

import dev.architectury.event.EventResult;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.genshinecraft.element.ElementalEntities;
import dev.genshinecraft.client.entity.BloomCoreRenderer;
import dev.genshinecraft.client.entity.CrystalShardRenderer;
import dev.genshinecraft.client.entity.ElementSlimeRenderer;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import dev.genshinecraft.item.WeaponBinding;
import dev.genshinecraft.world.StatueMenus;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.genshinecraft.world.StatueTeleportPacket;
import net.minecraft.client.KeyMapping;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
//? if >=1.21.1 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public final class GenshinecraftClient {
    //? if >=26.3 {
    /*private static final KeyMapping TELEPORT = new KeyMapping("key.genshinecraft.teleport",
            86, KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath("genshinecraft", "controls")));
    *///?} else {
    private static final KeyMapping TELEPORT =
            new KeyMapping("key.genshinecraft.teleport", 86, "key.categories.genshinecraft");
    //?}

    private GenshinecraftClient() {
    }

    public static void init() {
        DamageNumberSettings.load();
        KeyMappingRegistry.register(TELEPORT);
        ClientTickEvent.CLIENT_POST.register(client -> {
            //? if >=26.3 {
            /*boolean inGame = client.gui.screen() == null;
            *///?} else {
            boolean inGame = client.screen == null;
            //?}
            while (TELEPORT.consumeClick()) {
                if (inGame && client.player != null) {
                    ItemStack held = client.player.getMainHandItem();
                    if (held.is(CarrierItems.MOON_SIGIL.get()) || CarrierItems.isCarrier(held)) {
                        StatueTeleportPacket.send();
                    }
                }
            }
        });
        //? if >=26.3 {
        /*dev.architectury.registry.client.gui.MenuScreenRegistry.registerScreenFactory(
                StatueMenus.TYPE.get(), StatueOfferingScreen::new);
        *///?} else {
        MenuRegistry.registerScreenFactory(StatueMenus.TYPE.get(), StatueOfferingScreen::new);
        //?}
        EntityRendererRegistry.register(ElementalEntities.DENDRO_CORE::get, BloomCoreRenderer::new);
        EntityRendererRegistry.register(ElementalEntities.CRYSTAL_SHARD::get, CrystalShardRenderer::new);
        for (CarrierData.Element element : CarrierData.Element.values()) {
            var type = ElementalEntities.SLIMES.get(element);
            EntityRendererRegistry.register(type::get, context -> new ElementSlimeRenderer(context, element));
        }
        // The creative inventory's item-picker tabs bypass vanilla server-side slot
        // clicks. Intercept only clicks on real player inventory/hotbar slots.
        //? if >=26.3 {
        /*ClientScreenInputEvent.MOUSE_CLICKED_PRE.register((client, screen, click, doubleClick) ->
                tryCreativeBind(client, screen, click.x(), click.y(), click.button()));
        *///?} else {
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register((client, screen, x, y, button) ->
                tryCreativeBind(client, screen, x, y, button));
        //?}
        //? if =1.20.1 {
        /*ClientTooltipEvent.ITEM.register((stack, lines, flag) -> appendBindingTooltip(stack, lines));
        *///?} else {
        ClientTooltipEvent.ITEM.register((stack, lines, context, flag) -> appendBindingTooltip(stack, lines));
        //?}
        ClientGuiEvent.RENDER_HUD.register((graphics, delta) -> {
            Minecraft client = Minecraft.getInstance();
            //? if >=26.3 {
            /*boolean gameActive = client.gui.screen() == null;
            *///?} else {
            boolean gameActive = client.screen == null;
            //?}
            if (client.player == null || !gameActive) {
                return;
            }

            AuraIconOverlay.render(graphics);

            ItemStack boundCarrier = WeaponBinding.resolveStack(client.player.getMainHandItem(), client.player);
            if (boundCarrier.isEmpty()) {
                return;
            }

            int x = Math.max(4, graphics.guiWidth() - 24);
            int y = Math.max(4, (graphics.guiHeight() - 16) / 2);
            //? if >=26.3 {
            /*graphics.item(boundCarrier, x, y);
            *///?} else {
            graphics.renderItem(boundCarrier, x, y);
            //?}
        });
    }

    private static EventResult tryCreativeBind(Minecraft client, Screen screen,
                                               double mouseX, double mouseY, int button) {
        if (button != 1 || !(screen instanceof CreativeModeInventoryScreen creative)
                || client.player == null || client.gameMode == null
                || !client.player.getAbilities().instabuild) {
            return EventResult.pass();
        }
        // The creative item-picker and the creative inventory tab have separate
        // cursor stacks. Vanilla routes the inventory tab through inventoryMenu.
        ItemStack creativeCarried = creative.getMenu().getCarried();
        ItemStack inventoryCarried = client.player.inventoryMenu.getCarried();
        if (creativeCarried.isEmpty() && inventoryCarried.isEmpty()) {
            return EventResult.pass();
        }

        // All three supported versions use the vanilla creative screen's
        // 195x136 inventory backdrop. Slots store backdrop-relative positions.
        double localX = mouseX - (screen.width - 195) / 2.0;
        double localY = mouseY - (screen.height - 136) / 2.0;
        for (Slot slot : creative.getMenu().slots) {
            if (slot.container != client.player.getInventory()
                    || localX < slot.x - 1 || localX >= slot.x + 17
                    || localY < slot.y - 1 || localY >= slot.y + 17
                    || !slot.mayPickup(client.player)) {
                continue;
            }
            ItemStack inSlot = slot.getItem();
            ItemStack carried = creativeCarried;
            boolean carrierOnCursor = CarrierItems.isCarrier(carried) && WeaponBinding.isWeapon(inSlot);
            boolean carrierInSlot = CarrierItems.isCarrier(inSlot) && WeaponBinding.isWeapon(carried);
            if (!carrierOnCursor && !carrierInSlot) {
                carried = inventoryCarried;
                carrierOnCursor = CarrierItems.isCarrier(carried) && WeaponBinding.isWeapon(inSlot);
                carrierInSlot = CarrierItems.isCarrier(inSlot) && WeaponBinding.isWeapon(carried);
            }
            if (!carrierOnCursor && !carrierInSlot) {
                return EventResult.pass();
            }
            int syncSlot = -1;
            for (int i = 0; i < client.player.inventoryMenu.slots.size(); i++) {
                Slot inventorySlot = client.player.inventoryMenu.slots.get(i);
                if (inventorySlot.container == slot.container && inventorySlot.index == slot.index) {
                    syncSlot = i;
                    break;
                }
            }
            if (syncSlot < 0) {
                return EventResult.pass();
            }
            boolean bound = carrierOnCursor
                    ? WeaponBinding.bind(inSlot, carried, client.player)
                    : WeaponBinding.bind(carried, inSlot, client.player);
            if (bound) {
                slot.setChanged();
                client.gameMode.handleCreativeModeItemAdd(inSlot, syncSlot);
            }
            return EventResult.interruptTrue();
        }
        return EventResult.pass();
    }

    private static void appendBindingTooltip(ItemStack stack, List<Component> lines) {
        if (CarrierItems.isCarrier(stack)) {
            CarrierData organ = CarrierData.read(stack);
            String name = organ == null || organ.abandoned() || organ.owner() == null
                    ? "" : organ.ownerName();
            lines.add(name.isEmpty()
                    ? Component.translatable("tooltip.genshinecraft.no_owner").withStyle(ChatFormatting.GRAY)
                    : Component.translatable("tooltip.genshinecraft.owner", name).withStyle(ChatFormatting.GRAY));
            if (organ != null && !organ.statue().isBlank()) {
                lines.add(Component.translatable("tooltip.genshinecraft.teleport_carrier",
                        TELEPORT.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY));
            }
            if (organ != null && organ.elementalMastery() > 0) {
                lines.add(Component.translatable("tooltip.genshinecraft.offering_mastery",
                        organ.elementalMastery()).withStyle(ChatFormatting.GRAY));
            }
        }
        if (stack.is(CarrierItems.MOON_SIGIL.get())) {
            lines.add(Component.translatable("tooltip.genshinecraft.teleport_moon",
                    TELEPORT.getTranslatedKeyMessage()).withStyle(ChatFormatting.GRAY));
        }
        if (CarrierItems.isVision(stack)) {
            lines.add(Component.translatable("tooltip.genshinecraft.vision_style",
                    Component.translatable("region.genshinecraft." + CarrierItems.visionStyle(stack)))
                    .withStyle(ChatFormatting.GRAY));
        }
        WeaponBinding binding = WeaponBinding.read(stack);
        if (binding != null) {
            lines.add(Component.translatable("tooltip.genshinecraft.weapon_bound",
                    boundCarrierName(binding)).withStyle(style ->
                    style.withColor(TextColor.fromRgb(binding.element().color()))));
        }
    }

    private static Component boundCarrierName(WeaponBinding binding) {
        Component fallback = Component.translatable(binding.carrierNameKey());
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !client.player.getUUID().equals(binding.owner())) {
            return fallback;
        }

        UUID playerId = client.player.getUUID();
        Component cursorName = customNameIfBound(client.player.containerMenu.getCarried(), binding, playerId);
        if (cursorName != null) {
            return cursorName;
        }
        var inventory = client.player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            Component customName = customNameIfBound(inventory.getItem(slot), binding, playerId);
            if (customName != null) {
                return customName;
            }
        }
        return fallback;
    }

    private static Component customNameIfBound(ItemStack stack, WeaponBinding binding, UUID playerId) {
        CarrierData data = CarrierData.read(stack);
        if (data == null || !data.usableBy(playerId) || !data.id().equals(binding.carrierId())
                || data.element() != binding.element()) {
            return null;
        }
        //? if =1.20.1 {
        /*if (!stack.hasCustomHoverName()) {
            return null;
        }
        *///?} else {
        if (stack.get(DataComponents.CUSTOM_NAME) == null) {
            return null;
        }
        //?}
        return stack.getHoverName();
    }

}
