package dev.genshinecraft.mixin;

import dev.genshinecraft.Genshinecraft;
import dev.genshinecraft.item.CarrierData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if >=26.3 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Only taking the grindstone result commits abandonment, never previewing it. */
@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public abstract class GrindstoneAbandonMixin {
    @Inject(method = "onTake", at = @At("TAIL"))
    private void genshinecraft$abandon(Player player, ItemStack stack, CallbackInfo ci) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        CarrierData data = CarrierData.read(stack);
        if (data == null || !data.abandoned() || !player.getUUID().equals(data.owner())) return;

        serverPlayer.setHealth(1.0f);
        var manager = ((ServerLevel) serverPlayer.level()).getServer().getAdvancements();
        //? if >=26.3 {
        /*var advancement = manager.get(Identifier.fromNamespaceAndPath(
                Genshinecraft.MOD_ID, "embrace_lies_accept_truth"));
        *///?} else if =1.21.1 {
        var advancement = manager.get(ResourceLocation.fromNamespaceAndPath(
                Genshinecraft.MOD_ID, "embrace_lies_accept_truth"));
        //?} else {
        /*var advancement = manager.getAdvancement(new ResourceLocation(
                Genshinecraft.MOD_ID, "embrace_lies_accept_truth"));
        *///?}
        if (advancement != null) serverPlayer.getAdvancements().award(advancement, "abandon");
    }
}
