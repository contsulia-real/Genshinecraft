package dev.genshinecraft.mixin;

import dev.genshinecraft.world.StatueOfferings;
import dev.genshinecraft.world.WorldLevelMobs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.function.Consumer;

/** Bonus ordinary mob materials at the loot-table output, never extra equipment or rare drops. */
@Mixin(LootTable.class)
public abstract class WorldLevelLootMixin {
    @ModifyVariable(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;JLjava/util/function/Consumer;)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Consumer<ItemStack> genshinecraft$worldLoot(Consumer<ItemStack> output,
                                                       LootParams params, long seed,
                                                       Consumer<ItemStack> original) {
        //? if >=26.3 {
        /*Entity victim = params.contextMap().get(LootContextParams.THIS_ENTITY);
        boolean killed = params.contextMap().has(LootContextParams.DAMAGE_SOURCE);
        *///?} else {
        Entity victim = params.getOptionalParameter(LootContextParams.THIS_ENTITY);
        boolean killed = params.hasParam(LootContextParams.DAMAGE_SOURCE);
        //?}
        if (!killed || !WorldLevelMobs.eligible(victim)) return output;
        int worldLevel = StatueOfferings.get(params.getLevel()).worldLevel();
        if (worldLevel <= 0) return output;
        return stack -> {
            if (!ordinary(stack)) {
                output.accept(stack);
                return;
            }
            double bonus = stack.getCount() * (worldLevel * 0.08);
            int extra = (int) bonus;
            if (java.util.concurrent.ThreadLocalRandom.current().nextDouble() < bonus - extra) extra++;
            int remaining = stack.getCount() + extra;
            while (remaining > 0) {
                ItemStack copy = stack.copy();
                int count = Math.min(remaining, copy.getMaxStackSize());
                copy.setCount(count);
                output.accept(copy);
                remaining -= count;
            }
        };
    }

    private static boolean ordinary(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH) || stack.is(Items.BONE)
                || stack.is(Items.STRING) || stack.is(Items.GUNPOWDER)
                || stack.is(Items.SPIDER_EYE) || stack.is(Items.ENDER_PEARL)
                || stack.is(Items.SLIME_BALL) || stack.is(Items.BLAZE_ROD)
                || stack.is(Items.MAGMA_CREAM) || stack.is(Items.GHAST_TEAR)
                || stack.is(Items.PHANTOM_MEMBRANE);
    }
}
