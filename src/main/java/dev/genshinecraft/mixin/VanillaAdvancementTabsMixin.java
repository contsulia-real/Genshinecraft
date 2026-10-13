package dev.genshinecraft.mixin;

import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
//? if =1.20.1 {
/*import net.minecraft.advancements.Advancement;
*///?} else {
import net.minecraft.advancements.AdvancementNode;
//?}

/** Queue vanilla roots for their normal advancement packet, without granting a criterion. */
@Mixin(PlayerAdvancements.class)
public abstract class VanillaAdvancementTabsMixin {
    //? if =1.20.1 {
    /*@Shadow @Final private Set<Advancement> rootsToUpdate;
    *///?} else {
    @Shadow @Final private Set<AdvancementNode> rootsToUpdate;
    //?}
    @Unique private boolean genshinecraft$rootsQueued;

    //? if >=26.3 {
    /*@Inject(method = "flushDirty", at = @At("HEAD"))
    private void genshinecraft$showVanillaTabs(ServerPlayer player, boolean flush, CallbackInfo ci) {
        genshinecraft$queueRoots(player);
    }
    *///?} else {
    @Inject(method = "flushDirty", at = @At("HEAD"))
    private void genshinecraft$showVanillaTabs(ServerPlayer player, CallbackInfo ci) {
        genshinecraft$queueRoots(player);
    }
    //?}

    @Unique
    private void genshinecraft$queueRoots(ServerPlayer player) {
        if (genshinecraft$rootsQueued) return;
        var manager = ((ServerLevel) player.level()).getServer().getAdvancements();
        //? if =1.20.1 {
        /*for (Advancement root : manager.getAllAdvancements()) {
            if (root.getParent() == null && genshinecraft$isVanillaRoot(
                    root.getId().getNamespace(), root.getId().getPath())) {
                rootsToUpdate.add(root);
            }
        }
        *///?} else {
        for (AdvancementNode root : manager.tree().roots()) {
            var id = root.holder().id();
            if (genshinecraft$isVanillaRoot(id.getNamespace(), id.getPath())) {
                rootsToUpdate.add(root);
            }
        }
        //?}
        genshinecraft$rootsQueued = true;
    }

    @Unique
    private static boolean genshinecraft$isVanillaRoot(String namespace, String path) {
        return namespace.equals("minecraft") && (path.equals("story/root")
                || path.equals("adventure/root") || path.equals("nether/root")
                || path.equals("end/root") || path.equals("husbandry/root"));
    }
}
