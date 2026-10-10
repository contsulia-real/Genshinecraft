package dev.genshinecraft;

import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.genshinecraft.client.GenshinecraftClient;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.server.level.ServerPlayer;

public final class Genshinecraft {
    public static final String MOD_ID = "genshinecraft";

    private Genshinecraft() {
    }

    public static void init() {
        CarrierItems.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> GenshinecraftClient::init);
        TickEvent.PLAYER_POST.register(player -> {
            if (!(player instanceof ServerPlayer)) {
                return;
            }
            var inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                var stack = inventory.getItem(slot);
                if (CarrierItems.isCarrier(stack)) {
                    CarrierData.claim(stack, player.getUUID());
                }
            }
        });
    }
}
