package dev.genshinecraft.world;

import dev.genshinecraft.Genshinecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}

/** Grant a continuous World Level 0-9 advancement chain without resetting earlier milestones. */
public final class WorldLevelAdvancements {
    private WorldLevelAdvancements() {}

    //? if >=26.3 {
    /*private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Genshinecraft.MOD_ID, path);
    }
    *///?} else if =1.21.1 {
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Genshinecraft.MOD_ID, path);
    }
    //?} else {
    /*private static ResourceLocation id(String path) {
        return new ResourceLocation(Genshinecraft.MOD_ID, path);
    }
    *///?}

    public static void sync(ServerPlayer player, int level) {
        var manager = ((ServerLevel) player.level()).getServer().getAdvancements();
        var playerAdvancements = player.getAdvancements();
        for (int i = 0; i <= 9; i++) {
            //? if =1.20.1 {
            /*var marker = manager.getAdvancement(id("world_level_" + i));
            *///?} else {
            var marker = manager.get(id("world_level_" + i));
            //?}
            if (marker == null) continue;
            boolean completed = playerAdvancements.getOrStartProgress(marker).isDone();
            if (i <= level && !completed) {
                playerAdvancements.award(marker, "synced");
            } else if (i > level && completed) {
                // Administrative overrides can lower the effective world level.
                playerAdvancements.revoke(marker, "synced");
            }
        }
    }
}
