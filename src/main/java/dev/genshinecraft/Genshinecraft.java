package dev.genshinecraft;

import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.genshinecraft.client.GenshinecraftClient;
import dev.genshinecraft.element.ElementalEntities;
import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import dev.genshinecraft.item.WeaponBinding;
import dev.genshinecraft.particle.ElementalParticles;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

public final class Genshinecraft {
    public static final String MOD_ID = "genshinecraft";

    private Genshinecraft() {
    }

    public static void init() {
        CarrierItems.init();
        ElementalEntities.init();
        ElementalParticles.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> GenshinecraftClient::init);
        TickEvent.PLAYER_POST.register(player -> {
            if (!(player instanceof ServerPlayer)) {
                return;
            }
            if (player.containerMenu instanceof GrindstoneMenu grindstone) {
                updateGrindstone(grindstone);
            }
            var inventory = player.getInventory();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                var stack = inventory.getItem(slot);
                if (CarrierItems.isCarrier(stack)) {
                    CarrierData.claim(stack, player);
                }
            }
            ItemStack organ = ((dev.genshinecraft.item.OrganSlotHolder) player)
                    .genshinecraft$organSlot().getItem(0);
            if (CarrierItems.isCarrier(organ)) {
                CarrierData.claim(organ, player);
            }
        });
    }
    private static void updateGrindstone(GrindstoneMenu menu) {
        ItemStack inputA = menu.getSlot(0).getItem();
        ItemStack inputB = menu.getSlot(1).getItem();
        ItemStack output = menu.getSlot(2).getItem();
        // Vanilla leaves an unenchanted single input without an output.
        // An element-bound weapon still needs to be grindable by itself.
        if (output.isEmpty() && inputA.isEmpty() != inputB.isEmpty()) {
            ItemStack input = inputA.isEmpty() ? inputB : inputA;
            if (WeaponBinding.read(input) != null) {
                output = input.copy();
                output.setCount(1);
                WeaponBinding.clear(output);
                menu.getSlot(2).set(output);
                menu.broadcastChanges();
            }
        } else if (!output.isEmpty() && WeaponBinding.clear(output)) {
            menu.getSlot(2).setChanged();
            menu.broadcastChanges();
        }
    }

}
