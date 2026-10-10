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

            int rows = Math.min(carriers.size(), Math.min(7, Math.max(1, (graphics.guiHeight() - 60) / 12)));
            int start = carriers.isEmpty() ? 0 : Math.max(0, Math.min(index - rows / 2, carriers.size() - rows));
            List<String> lines = new ArrayList<>();
            for (int i = start; i < start + rows; i++) {
                CarrierEntry entry = carriers.get(i);
                String element = Component.translatable("element.genshinecraft."
                        + entry.data().element().name().toLowerCase(Locale.ROOT)).getString();
                lines.add((i == index ? "> " : "  ") + entry.stack().getHoverName().getString()
                        + " [" + element + "]  E:-- CD:--");
            }
            int width = Math.max(client.font.width(title), client.font.width(status));
            for (String line : lines) {
                width = Math.max(width, client.font.width(line));
            }
            width = Math.min(width + 12, graphics.guiWidth() - 12);
            int height = 31 + lines.size() * 12;
            graphics.fill(6, 16, 6 + width, 16 + height, 0xAA10141E);

            //? if >=26.3 {
            /*graphics.text(client.font, title, 12, 21, 0xFFFFFFFF);
            *///?} else {
            graphics.drawString(client.font, title, 12, 21, 0xFFFFFFFF);
            //?}
            for (int i = 0; i < lines.size(); i++) {
                //? if >=26.3 {
                /*graphics.text(client.font, lines.get(i), 12, 33 + i * 12, 0xFFFFFFFF);
                *///?} else {
                graphics.drawString(client.font, lines.get(i), 12, 33 + i * 12, 0xFFFFFFFF);
                //?}
            }
            //? if >=26.3 {
            /*graphics.text(client.font, status, 12, 33 + lines.size() * 12, 0xFFB2B9C5);
            *///?} else {
            graphics.drawString(client.font, status, 12, 33 + lines.size() * 12, 0xFFB2B9C5);
            //?}
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
}
