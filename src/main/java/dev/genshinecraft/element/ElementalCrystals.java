package dev.genshinecraft.element;

import dev.genshinecraft.item.CarrierData.Element;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/** Stationary, tagged crystallize drops: at most 3 nearby, lasting 15 seconds. */
public final class ElementalCrystals {
    private static final String SHARD_TAG = "genshinecraft:crystallize_shard";
    private static final int MAX_SHARDS = 3;
    private static final int LIFE_TICKS = 300;

    private ElementalCrystals() {
    }

    public static boolean isShard(ItemEntity entity) {
        return entity.getCustomName() != null
                && SHARD_TAG.equals(entity.getCustomName().getString());
    }

    public static void drop(ServerLevel level, LivingEntity target, Element element) {
        var item = CarrierItems.CRYSTAL_SHARDS.get(element);
        if (item == null) {
            return;
        }
        List<ItemEntity> nearby = new java.util.ArrayList<>();
        for (var entity : level.getAllEntities()) {
            if (entity instanceof ItemEntity itemEntity && isShard(itemEntity)) {
                nearby.add(itemEntity);
            }
        }
        // Strict cap of 3: immediately retire the oldest instead of leaving surplus
        // shards around for the approximately 2-second Wiki cleanup window.
        if (nearby.size() >= MAX_SHARDS) {
            nearby.stream().max(Comparator.comparingInt(e -> e.tickCount))
                    .ifPresent(ItemEntity::discard);
        }

        ItemEntity crystal = new ItemEntity(level, target.getX(),
                target.getY() + 0.25, target.getZ(), new ItemStack(item.get()));
        crystal.setCustomName(Component.literal(SHARD_TAG));
        crystal.setCustomNameVisible(false);
        crystal.setNoGravity(true);
        crystal.setDeltaMovement(Vec3.ZERO);
        crystal.setPickUpDelay(10);
        level.addFreshEntity(crystal);
    }

    public static void tick(ItemEntity entity) {
        if (!isShard(entity) || !(entity.level() instanceof ServerLevel)) {
            return;
        }
        entity.setNoGravity(true);
        entity.setDeltaMovement(Vec3.ZERO);
        if (entity.tickCount >= LIFE_TICKS) {
            entity.discard();
        }
    }

    /** Crystallize shards shield on pickup; ordinary copies of the item do not. */
    public static boolean pickup(ItemEntity item, Player player) {
        if (!(player.level() instanceof ServerLevel) || !isShard(item) || item.tickCount < 10) {
            return false;
        }
        for (var registered : CarrierItems.CRYSTAL_SHARDS.values()) {
            if (item.getItem().is(registered.get())) {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                        300, 0, false, true));
                item.discard();
                return true;
            }
        }
        return false;
    }
}
