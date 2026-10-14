package dev.genshinecraft.mixin;

import dev.genshinecraft.item.CarrierItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla grindstone inputs reject non-damageable, unenchanted items. Only organs are exempt. */
@Mixin(targets = {
        "net.minecraft.world.inventory.GrindstoneMenu$2",
        "net.minecraft.world.inventory.GrindstoneMenu$3"
})
public abstract class GrindstoneOrganInputMixin {
    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void genshinecraft$acceptOrgan(ItemStack stack, CallbackInfoReturnable<Boolean> result) {
        if (CarrierItems.isCarrier(stack)) {
            result.setReturnValue(true);
        }
    }
}
