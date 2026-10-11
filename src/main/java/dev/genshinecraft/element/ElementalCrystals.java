package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Four element-colored crystallize shards with pickup absorption. */
public final class ElementalCrystals {
    private ElementalCrystals() {
    }

    public static void drop(ServerLevel level, LivingEntity target, Element element) {
        var item = CarrierItems.CRYSTAL_SHARDS.get(element);
        if (item == null) {
            return;
        }
        ItemEntity crystal = new ItemEntity(level, target.getX(),
                target.getY() + target.getBbHeight() * 0.45, target.getZ(),
                new ItemStack(item.get()));
        crystal.setPickUpDelay(10);
        level.addFreshEntity(crystal);
    }

    /** Return true to prevent the shard also being added to the inventory. */
    public static boolean pickup(ItemEntity item, Player player) {
        if (!(player.level() instanceof ServerLevel)) {
            return false;
        }
        for (var registered : CarrierItems.CRYSTAL_SHARDS.values()) {
            if (item.getItem().is(registered.get())) {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                        200, 0, false, true));
                item.discard();
                return true;
            }
        }
        return false;
    }
}
