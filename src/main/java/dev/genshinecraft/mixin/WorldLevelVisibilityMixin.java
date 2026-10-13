package dev.genshinecraft.mixin;

import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
//? if =1.20.1 {
/*import net.minecraft.advancements.Advancement;
*///?} else {
import net.minecraft.advancements.AdvancementNode;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

/**
 * The vanilla advancements UI stays intact. Expose the full 0-9 progression and the
 * original five vanilla tabs without granting any unfinished advancement criteria.
 */
@Mixin(AdvancementVisibilityEvaluator.class)
public abstract class WorldLevelVisibilityMixin {
    //? if =1.20.1 {
    /*@Redirect(
            method = "evaluateVisibility(Lnet/minecraft/advancements/Advancement;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean genshinecraft$visible(Predicate<Advancement> complete, Object candidate) {
        Advancement advancement = (Advancement) candidate;
        return genshinecraft$show(advancement.getId().getNamespace(), advancement.getId().getPath())
                || complete.test(advancement);
    }
    *///?} else {
    @Redirect(
            method = "evaluateVisibility(Lnet/minecraft/advancements/AdvancementNode;Lit/unimi/dsi/fastutil/Stack;Ljava/util/function/Predicate;Lnet/minecraft/server/advancements/AdvancementVisibilityEvaluator$Output;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean genshinecraft$visible(Predicate<AdvancementNode> complete, Object candidate) {
        AdvancementNode node = (AdvancementNode) candidate;
        var id = node.holder().id();
        return genshinecraft$show(id.getNamespace(), id.getPath()) || complete.test(node);
    }
    //?}

    @Unique
    private static boolean genshinecraft$show(String namespace, String path) {
        if (namespace.equals("genshinecraft")) {
            return path.matches("world_level_[0-9]");
        }
        if (!namespace.equals("minecraft")) return false;
        // Preserve the five vanilla root tabs even before a player unlocks their first advancement.
        return path.equals("story/root") || path.equals("adventure/root")
                || path.equals("nether/root") || path.equals("end/root")
                || path.equals("husbandry/root");
    }
}
