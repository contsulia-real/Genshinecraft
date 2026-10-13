package dev.genshinecraft.world;

import dev.genshinecraft.item.CarrierData;
import dev.genshinecraft.item.CarrierItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

/** Teleportation is fully server-authoritative: an empty network request never names coordinates. */
public final class StatueTeleport {
    private StatueTeleport() {}

    public static void execute(ServerPlayer player) {
        ItemStack held = player.getMainHandItem();
        ServerLevel destination;
        BlockPos altar;
        if (held.is(CarrierItems.MOON_SIGIL.get())) {
            destination = (ServerLevel) player.level();
            altar = StatueOfferings.get(destination).nearestMoon(destination, player.blockPosition());
            if (altar == null) {
                player.sendSystemMessage(Component.translatable("message.genshinecraft.teleport.no_moon"));
                return;
            }
            if (destination.getBlockState(altar).getBlock() != StatueBlocks.MOON_ALTAR.get()) return;
        } else {
            CarrierData data = CarrierData.read(held);
            if (data == null || !data.usableBy(player.getUUID()) || data.statue().isBlank()) {
                player.sendSystemMessage(Component.translatable("message.genshinecraft.teleport.invalid"));
                return;
            }
            destination = null;
            altar = null;
            for (ServerLevel possible : ((ServerLevel) player.level()).getServer().getAllLevels()) {
                String prefix = possible.dimension() + "@";
                if (!data.statue().startsWith(prefix)) continue;
                String[] xyz = data.statue().substring(prefix.length()).split(",");
                if (xyz.length != 3) break;
                try {
                    altar = new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]),
                            Integer.parseInt(xyz[2]));
                    destination = possible;
                } catch (NumberFormatException ignored) {
                }
                break;
            }
            if (destination == null || altar == null || destination.getBlockState(altar).getBlock()
                    != StatueBlocks.ALTARS.get(data.element()).get()) {
                player.sendSystemMessage(Component.translatable("message.genshinecraft.teleport.invalid"));
                return;
            }
        }

        BlockPos feet = findSafePosition(destination, altar);
        if (feet == null) {
            player.sendSystemMessage(Component.translatable("message.genshinecraft.teleport.unsafe"));
            return;
        }
        //? if >=26.3 {
        /*player.teleportTo(destination, feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5,
                Set.of(), player.getYRot(), player.getXRot(), false);
        *///?} else {
        player.teleportTo(destination, feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5,
                player.getYRot(), player.getXRot());
        //?}
    }

    /** Search near the shrine for two blocks of air over solid ground. No clipping into the statue. */
    private static BlockPos findSafePosition(ServerLevel level, BlockPos center) {
        for (int radius = 2; radius <= 10; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    for (int dy = -5; dy <= 10; dy++) {
                        BlockPos foot = center.offset(dx, dy, dz);
                        if (!level.getBlockState(foot).isAir() || !level.getBlockState(foot.above()).isAir()
                                || level.getBlockState(foot.below()).isAir()) continue;
                        if (!level.getBlockState(foot.below())
                                .isFaceSturdy(level, foot.below(), net.minecraft.core.Direction.UP)) continue;
                        return foot;
                    }
                }
            }
        }
        return null;
    }
}
