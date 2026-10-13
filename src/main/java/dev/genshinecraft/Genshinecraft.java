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
import dev.genshinecraft.world.ElementOres;
import dev.genshinecraft.world.StatueBlocks;
import dev.genshinecraft.world.StatueOfferings;
import dev.genshinecraft.world.WorldLevelAdvancements;
import dev.genshinecraft.world.WorldLevelCommands;
import dev.genshinecraft.world.StatueOfferingMenu;
import dev.genshinecraft.world.StatueMenus;
import dev.genshinecraft.world.StatueTeleportPacket;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

public final class Genshinecraft {
    public static final String MOD_ID = "genshinecraft";

    private Genshinecraft() {
    }

    public static void init() {
        ElementOres.init();
        StatueBlocks.init();
        StatueMenus.init();
        WorldLevelCommands.register();
        StatueTeleportPacket.init();
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
            // Keep one server-wide XP snapshot per UUID; offline players retain their last level.
            StatueOfferings.get((ServerLevel) player.level()).trackExperience(player);
            if (player.tickCount % 20 == 0) {
                StatueOfferings.syncCarriers(player);
                WorldLevelAdvancements.sync((ServerPlayer) player,
                        StatueOfferings.get((ServerLevel) player.level()).worldLevel());
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
