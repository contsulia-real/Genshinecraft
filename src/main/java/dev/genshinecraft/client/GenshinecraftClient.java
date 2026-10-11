package dev.genshinecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.genshinecraft.element.ElementalEntities;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import dev.architectury.event.events.client.ClientScreenInputEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import dev.genshinecraft.item.WeaponBinding;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
//? if >=1.21.1 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
//? if =1.20.1 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else if =1.21.1 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.world.item.ItemStack;
//? if <26.3 {
import org.lwjgl.glfw.GLFW;
//?}

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class GenshinecraftClient {
    private static final KeyMapping SWITCH_CARRIER = new KeyMapping(
            //? if >=26.3 {
            /*"key.genshinecraft.switch_carrier", InputConstants.Type.KEYBOARD, InputConstants.KEY_V,
            *///?} else {
            "key.genshinecraft.switch_carrier", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V,
            //?}
            //? if >=26.3 {
            /*KeyMapping.Category.register(Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, "carrier"))
            *///?} else {
            "key.categories.genshinecraft"
            //?}
    );

    private static UUID selectedId;
    private static UUID currentPlayerId;

    private GenshinecraftClient() {
    }

    @SuppressWarnings("unchecked")
    private static EntityType<Display.TextDisplay> displayType(EntityType<?> type) {
        return (EntityType<Display.TextDisplay>) type;
    }

    public static void init() {
        DamageNumberSettings.load();
        EntityRendererRegistry.register(() -> displayType(ElementalEntities.DENDRO_CORE.get()),
                context -> new DisplayRenderer.TextDisplayRenderer(context) {});
        EntityRendererRegistry.register(() -> displayType(ElementalEntities.CRYSTAL_SHARD.get()),
                context -> new DisplayRenderer.TextDisplayRenderer(context) {});
        KeyMappingRegistry.register(SWITCH_CARRIER);
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
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.player == null) {
                selectedId = null;
                currentPlayerId = null;
                return;
            }
            UUID playerId = client.player.getUUID();
            if (!playerId.equals(currentPlayerId)) {
                currentPlayerId = playerId;
                selectedId = null;
            }

            List<CarrierEntry> carriers = availableCarriers(client);
            int index = currentIndex(carriers);
            while (SWITCH_CARRIER.consumeClick()) {
                //? if >=26.3 {
                /*boolean gameActive = client.gui.screen() == null;
                *///?} else {
                boolean gameActive = client.screen == null;
                //?}
                if (gameActive && index >= 0) {
                    index = (index + 1) % carriers.size();
                    selectedId = carriers.get(index).data().id();
                }
            }
        });
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

            List<CarrierEntry> carriers = availableCarriers(client);
            int index = currentIndex(carriers);
            if (index < 0) {
                return;
            }

            CarrierEntry selected = carriers.get(index);
            int x = Math.max(4, graphics.guiWidth() - 96);
            int y = Math.max(4, (graphics.guiHeight() - 16) / 2);
            //? if >=26.3 {
            /*graphics.item(selected.stack(), x, y);
            *///?} else {
            graphics.renderItem(selected.stack(), x, y);
            //?}
            drawEnergyBar(graphics, selected.data().element(), x + 20, y + 4,
                    client.level == null ? 0L : client.level.getGameTime());
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
        }
        if (CarrierItems.isVision(stack)) {
            lines.add(Component.translatable("tooltip.genshinecraft.vision_style",
                    Component.translatable("region.genshinecraft.mondstadt"))
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
        ItemStack equipped = ((dev.genshinecraft.item.OrganSlotHolder) client.player)
                .genshinecraft$organSlot().getItem(0);
        Component equippedName = customNameIfBound(equipped, binding, playerId);
        return equippedName == null ? fallback : equippedName;
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

    private static int currentIndex(List<CarrierEntry> carriers) {
        if (carriers.isEmpty()) {
            selectedId = null;
            return -1;
        }
        for (int i = 0; i < carriers.size(); i++) {
            if (carriers.get(i).data().id().equals(selectedId)) {
                return i;
            }
        }
        selectedId = carriers.get(0).data().id();
        return 0;
    }

    private static List<CarrierEntry> availableCarriers(Minecraft client) {
        List<CarrierEntry> carriers = new ArrayList<>();
        UUID ownerId = client.player.getUUID();
        var inventory = client.player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            CarrierData data = CarrierData.read(stack);
            if (data != null && data.usableBy(ownerId)) {
                carriers.add(new CarrierEntry(stack, data));
            }
        }
        ItemStack equipped = ((dev.genshinecraft.item.OrganSlotHolder) client.player)
                .genshinecraft$organSlot().getItem(0);
        CarrierData organ = CarrierData.read(equipped);
        if (organ != null && organ.usableBy(ownerId)) {
            carriers.add(new CarrierEntry(equipped, organ));
        }
        return carriers;
    }

    private record CarrierEntry(ItemStack stack, CarrierData data) {
    }
    private static void drawEnergyBar(
            //? if >=26.3 {
            /*net.minecraft.client.gui.GuiGraphicsExtractor graphics,
            *///?} else {
            net.minecraft.client.gui.GuiGraphics graphics,
            //?}
            CarrierData.Element element, int x, int y, long gameTime) {
        String name = element.name().toLowerCase(Locale.ROOT);
        String base = "textures/item/energy/bar/energy_bar_" + name;
        int frame = (int) ((gameTime / 3L) % 8L); // energy_bar_*_fill.png.mcmeta: frametime 3, eight 64x8 frames

        //? if >=26.3 {
        /*Identifier background = Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, base + "_bg.png");
        Identifier fill = Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, base + "_fill.png");
        graphics.blit(background, x, y, 64, 8, 0f, 1f, 0f, 1f);
        graphics.blit(fill, x, y, 64, 8, 0f, 1f, frame / 8f, (frame + 1) / 8f);
        *///?} else if =1.21.1 {
        ResourceLocation background = ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, base + "_bg.png");
        ResourceLocation fill = ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, base + "_fill.png");
        graphics.blit(background, x, y, 0f, 0f, 64, 8, 64, 8);
        graphics.blit(fill, x, y, 0f, (float) (frame * 8), 64, 8, 64, 64);
        //?} else {
        /*ResourceLocation background = new ResourceLocation(Genshinecraft.MOD_ID, base + "_bg.png");
        ResourceLocation fill = new ResourceLocation(Genshinecraft.MOD_ID, base + "_fill.png");
        graphics.blit(background, x, y, 0f, 0f, 64, 8, 64, 8);
        graphics.blit(fill, x, y, 0f, (float) (frame * 8), 64, 8, 64, 64);
        *///?}
    }

}
