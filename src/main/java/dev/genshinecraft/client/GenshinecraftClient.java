package dev.genshinecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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

    public static void init() {
        KeyMappingRegistry.register(SWITCH_CARRIER);
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
            String title = carriers.isEmpty()
                    ? Component.translatable("hud.genshinecraft.empty").getString()
                    : Component.translatable("hud.genshinecraft.header",
                            index + 1, carriers.size(), SWITCH_CARRIER.getTranslatedKeyMessage().getString()).getString();
            String status = carriers.isEmpty()
                    ? Component.translatable("hud.genshinecraft.no_carrier").getString()
                    : Component.translatable("hud.genshinecraft.pending").getString();

            // Keep the list centered vertically against the right edge of the HUD.
            int rows = Math.min(carriers.size(), Math.min(7, Math.max(1, (graphics.guiHeight() - 42) / 30)));
            int start = carriers.isEmpty() ? 0 : Math.max(0, Math.min(index - rows / 2, carriers.size() - rows));
            int panelWidth = 172;
            int x = Math.max(4, graphics.guiWidth() - panelWidth - 10);
            int height = 24 + rows * 30 + 12;
            int y = Math.max(4, (graphics.guiHeight() - height) / 2);

            drawText(graphics, client, title, Math.max(4, graphics.guiWidth() - client.font.width(title) - 10), y, 0xFFFFFFFF);
            for (int i = 0; i < rows; i++) {
                CarrierEntry entry = carriers.get(start + i);
                int rowY = y + 18 + 30 * i;
                boolean selected = start + i == index;
                if (selected) {
                    drawText(graphics, client, ">", x, rowY + 3, 0xFFFFD875);
                }
                //? if >=26.3 {
                /*graphics.item(entry.stack(), x + 11, rowY);
                *///?} else {
                graphics.renderItem(entry.stack(), x + 11, rowY);
                //?}
                drawText(graphics, client, entry.stack().getHoverName().getString(),
                        x + 31, rowY + 1, selected ? 0xFFFFD875 : 0xFFFFFFFF);
                drawEnergyBar(graphics, entry.data().element(), x + 31, rowY + 15,
                        client.level == null ? 0L : client.level.getGameTime());
                drawText(graphics, client, "--", x + 100, rowY + 14, 0xFFB2B9C5);
            }
            drawText(graphics, client, status,
                    Math.max(4, graphics.guiWidth() - client.font.width(status) - 10),
                    y + 19 + rows * 30, 0xFFB2B9C5);
        });
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
        return carriers;
    }

    private record CarrierEntry(ItemStack stack, CarrierData data) {
    }
    // HUD preview: there is no server-side elemental-energy amount yet.
    // The full animated fill is displayed as artwork, never as a numeric percentage.
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

    private static void drawText(
            //? if >=26.3 {
            /*net.minecraft.client.gui.GuiGraphicsExtractor graphics,
            *///?} else {
            net.minecraft.client.gui.GuiGraphics graphics,
            //?}
            Minecraft client, String value, int x, int y, int color) {
        //? if >=26.3 {
        /*graphics.text(client.font, value, x, y, color);
        *///?} else {
        graphics.drawString(client.font, value, x, y, color);
        //?}
    }

}
