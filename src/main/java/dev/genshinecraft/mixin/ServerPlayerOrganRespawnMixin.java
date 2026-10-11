package dev.genshinecraft.mixin;

import dev.genshinecraft.item.OrganSlotHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
//? if >=26.3 {
/*import net.minecraft.world.level.gamerules.GameRules;
*///?} else {
import net.minecraft.world.level.GameRules;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Make the organ obey vanilla keepInventory/death rules. */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerOrganRespawnMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void genshinecraft$dropOrganOnDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        //? if >=26.3 {
        /*boolean keepInventory = player.level().getGameRules().get(GameRules.KEEP_INVENTORY);
        *///?} else {
        boolean keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        //?}
        if (keepInventory) {
            return;
        }
        var slot = ((OrganSlotHolder) player).genshinecraft$organSlot();
        ItemStack equipped = slot.getItem(0);
        if (!equipped.isEmpty()) {
            ItemEntity dropped = new ItemEntity(player.level(), player.getX(),
                    player.getY() + 0.25, player.getZ(), equipped.copy());
            slot.setItem(0, ItemStack.EMPTY);
            player.level().addFreshEntity(dropped);
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void genshinecraft$copyOrganWhenKept(ServerPlayer previous, boolean keepInventory,
                                                  CallbackInfo ci) {
        if (keepInventory) {
            ((OrganSlotHolder) (Object) this).genshinecraft$organSlot().setItem(0,
                    ((OrganSlotHolder) previous).genshinecraft$organSlot().getItem(0).copy());
        }
    }
}
