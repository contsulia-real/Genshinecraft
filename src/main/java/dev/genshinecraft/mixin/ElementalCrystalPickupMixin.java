package dev.genshinecraft.mixin;

import dev.genshinecraft.element.ElementalCrystals;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ElementalCrystalPickupMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$crystalShield(Player player, CallbackInfo ci) {
        if (ElementalCrystals.pickup((ItemEntity) (Object) this, player)) {
            ci.cancel();
        }
    }
}
